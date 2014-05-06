package kitty.kaf.fileupload;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import kitty.kaf.io.DataReadStream;

public class HttpInputStream extends InputStream {
	byte[] boundary;
	InputStream in;
	LinkedList<Byte> queue = null;
	Byte first, last;
	boolean allReaded = false;
	int timeout = 300000;
	long dataSize;
	HttpFieldItem currentField;
	boolean enabledRead = false;

	public HttpInputStream(String boundary, InputStream in) {
		super();
		this.boundary = ("\r\n--" + boundary).getBytes();
		this.in = in;
		first = this.boundary[0];
		last = this.boundary[this.boundary.length - 1];
		dataSize = 0;
	}

	public HttpInputStream(HttpServletRequest request) throws IOException {
		super();
		this.boundary = ("\r\n--" + getBoundary(request)).getBytes();
		this.in = request.getInputStream();
		first = this.boundary[0];
		last = this.boundary[this.boundary.length - 1];
		dataSize = 0;
		skipFirstBoundary();
	}

	private String getBoundary(HttpServletRequest request) {
		int index = request.getContentType().indexOf("boundary=");
		String ret = request.getContentType().substring(index + 9).trim();
		return ret;
	}

	public long getDataSize() {
		return dataSize;
	}

	public HttpFieldItem getCurrentField() {
		return currentField;
	}

	@Override
	public void close() throws IOException {
		if (in != null)
			in.close();
	}

	private int readIn() throws IOException {
		int c = in.read();
		// System.out.println(c + "-(" + ((char) c) + ")");
		return c;
	}

	// 跳过开始的boundary
	public void skipFirstBoundary() throws IOException {
		for (int i = 0; i < boundary.length; i++)
			if (readIn() < 0)
				throw new IOException("已达流末尾");
	}

	@Override
	public int read() throws IOException {
		if (!enabledRead)
			return -1;

		if (queue == null) {
			queue = new LinkedList<Byte>();
			for (int i = 0; i < boundary.length; i++) {
				int r = readIn();
				if (r == -1) {
					queue = null;
					enabledRead = false;
					return r;
				}
				queue.offer((byte) r);
			}
			dataSize = queue.size();
		}
		if (queue.getFirst().equals(first) && queue.getLast().equals(last)) {
			// 比较整个boundary
			boolean same = true;
			for (int i = 1; i < boundary.length - 1; i++) {
				if (!queue.get(i).equals(boundary[i])) {
					same = false;
					break;
				}
			}
			if (same) {
				queue.clear();
				queue = null;
				int c = readIn();
				int c1 = readIn();
				if (c == '-' || c1 == '-') {// {boundary}--结尾表示报文结尾
					allReaded = true;
				}
				enabledRead = false;
				return -1;
			}
		}
		dataSize++;
		if (currentField.isFileField() && currentField.maxSize > 0 && dataSize > currentField.maxSize)
			throw new IOException("上传的文件太大");
		int c = readIn();
		if (c < 0) {
			allReaded = true;
			enabledRead = false;
			return -1;
		}
		queue.offer((byte) c);
		return queue.poll() & 0xff;// 将负的字节转换为正的整数，主要是处理汉字等双字节字串
	}

	// 读取字段头，字段头到一个空白行为止
	public HttpFieldItem readNextHeaders() throws UnsupportedEncodingException, IOException {
		HttpFieldItem header = new HttpFieldItem();
		DataReadStream reader = new DataReadStream(in, timeout);
		String line;
		while (!(line = new String(reader.readln("\r\n".getBytes(), false), "utf-8")).trim().isEmpty()) {
			if (line.startsWith("Content-Disposition:")) {
				int index = line.indexOf(";");
				if (index < 0)
					index = line.length();
				header.contentDisposition = line.substring(20, index).trim();
				if (index == line.length())
					line = "";
				else
					line = line.substring(index + 1);
			} else if (line.startsWith("Content-Type:")) {
				int index = line.indexOf(";");
				if (index < 0)
					index = line.length();
				header.contentType = line.substring(13, index).trim();
				if (index == line.length())
					line = "";
				else
					line = line.substring(index + 1);
			}
			int index = line.indexOf(" name=\"");
			if (index >= 0) {
				int end = line.indexOf("\"", index + 7);
				if (end < 0)
					end = line.length();
				header.name = line.substring(index + 7, end);
			}
			index = line.indexOf(" filename=\"");
			if (index >= 0) {
				int end = line.indexOf("\"", index + 11);
				if (end < 0)
					end = line.length();
				header.fileName = line.substring(index + 11, end);
			}
		}
		currentField = header;
		enabledRead = true;
		return header;
	}

	// 标示是否已经全部读取完毕
	public boolean isAllReaded() {
		return allReaded;
	}

	// 读取下一个字段的数据
	public void readNextData(OutputStream stream) throws IOException {
		int c;
		while ((c = read()) >= 0)
			stream.write(c);
	}

	public String readNextString(String encode) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		readNextData(out);
		return out.toString(encode);
	}

	public static void main(String[] args) throws IOException {
		HttpInputStream stream = null;
		try {
			stream = new HttpInputStream("----WebKitFormBoundaryKhAr1p8dqaBkvvD7", new FileInputStream(
					"/zhaom/aaa-0.dat"));
			stream.skipFirstBoundary();
			while (!stream.isAllReaded()) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				stream.readNextHeaders();
				stream.readNextData(out);
				System.out.print(new String(out.toByteArray(), "utf-8"));
				System.out.println("===============");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			stream.close();
		}
	}
}
