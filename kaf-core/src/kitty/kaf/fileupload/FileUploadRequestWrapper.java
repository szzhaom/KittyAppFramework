package kitty.kaf.fileupload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 * 
 * @author 赵明
 */
abstract public class FileUploadRequestWrapper extends HttpServletRequestWrapper {

	private Map<String, String[]> params = new HashMap<String, String[]>();
	private Map<String, List<FileUploader>> files = new HashMap<String, List<FileUploader>>();

	protected abstract FileUploader getUploader(HttpServletRequest request, FileItemStream item) throws Throwable;

	public FileUploadRequestWrapper(int maxFileSize, HttpServletRequest request) {
		super(request);
		try {
			ServletFileUpload upload = new ServletFileUpload();
			upload.setFileSizeMax(maxFileSize);
			FileItemIterator it = upload.getItemIterator(request);
			request.setAttribute("uploadfiles", files);
			while (it.hasNext()) {
				FileItemStream item = it.next();
				InputStream stream = item.openStream();
				if (item.isFormField()) {
					String[] values = this.params.get(item.getFieldName());
					if (values == null) {
						values = new String[1];
						this.params.put(item.getFieldName(), values);
					} else {
						String[] ov = values;
						values = new String[ov.length + 1];
						for (int i = 0; i < ov.length; i++)
							values[i] = ov[i];
					}
					values[values.length - 1] = Streams.asString(stream, "utf-8");
				} else {
					FileUploader uploader = getUploader(request, item);
					if (uploader != null) {
						List<FileUploader> values = files.get(item.getFieldName());
						if (values == null) {
							values = new ArrayList<FileUploader>();
							files.put(item.getFieldName(), values);
						}
						values.add(uploader);
						uploader.upload(stream);
						if (!uploader.getOriginalName().equals(uploader.getName())) {
							this.params.put("original_" + item.getFieldName(),
									new String[] { uploader.getOriginalName() });
						}
					}
				}
			}
		} catch (FileUploadException fe) {
		} catch (Throwable ne) {
			throw new RuntimeException(ne);
		}
	}

	@Override
	public String getParameter(String name) {
		String[] r = params.get(name);
		if (r != null) {
			return r[0];
		} else {
			List<FileUploader> r1 = files.get(name);
			if (r1 != null)
				return r1.get(0).getName();
			return super.getParameter(name);
		}
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return params;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(params.keySet());
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return params.get(arg0);
	}

	/**
	 * 获取一个文件上传对象
	 * 
	 * @param name
	 *            文件参数名
	 * @return 文件对象，为null，则表示没有这个参数
	 */
	public List<FileUploader> getFileItemList(String name) {
		return files.get(name);
	}

	/**
	 * 取出第1个文件列表
	 * 
	 */
	public List<FileUploader> getFirstFileItemList() {
		Iterator<String> it = files.keySet().iterator();
		if (it.hasNext())
			return files.get(it.next());
		else
			return null;
	}

	/**
	 * 获取文件映射表
	 * 
	 * @return
	 */
	public Map<String, List<FileUploader>> getFileItemListMap() {
		return files;
	}
}
