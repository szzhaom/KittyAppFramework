package kitty.kaf.fileupload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 
 * @author 赵明
 */

abstract public class FileUploadRequestWrapper extends HttpServletRequestWrapper {

	protected Map<String, String[]> params = new HashMap<String, String[]>();
	protected Map<String, List<FileUploader>> files = new HashMap<String, List<FileUploader>>();

	protected abstract FileUploader getUploader(HttpServletRequest request, HttpFieldItem item) throws Throwable;

	protected void processField(String fieldName, String text, HashMap<String, List<String>> params) throws Throwable {
		List<String> values = params.get(fieldName);
		if (values == null) {
			values = new ArrayList<String>();
			params.put(fieldName, values);
		}
		values.add(text);
	}

	public FileUploadRequestWrapper(long maxFileSize, HttpServletRequest request) {
		super(request);
		try {
			HttpInputStream stream = new HttpInputStream(request);
			request.setAttribute("uploadfiles", files);
			HashMap<String, List<String>> ps = new HashMap<String, List<String>>();
			while (!stream.isAllReaded()) {
				HttpFieldItem item = stream.readNextHeaders();
				if (!item.isFileField()) {
					processField(item.getName(), stream.readNextString("utf-8"), ps);
				} else {

					FileUploader uploader = getUploader(request, item);
					if (uploader != null) {
						List<FileUploader> values = files.get(item.getName());
						if (values == null) {
							values = new ArrayList<FileUploader>();
							files.put(item.getName(), values);
						}
						values.add(uploader);
						item.maxSize = maxFileSize;
						uploader.upload(stream);
						if (!uploader.getOriginalName().equals(uploader.getName())) {
							this.params.put("original_" + item.getName(), new String[] { uploader.getOriginalName() });
						}
					}
				}
			}
			Iterator<String> it1 = ps.keySet().iterator();
			while (it1.hasNext()) {
				String key = it1.next();
				this.params.put(key, ps.get(key).toArray(new String[0]));
			}
			ps.clear();
			ps = null;
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
