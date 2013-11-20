/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 
 * @author zhaom
 */
public class FileUploadRequestWrapper extends HttpServletRequestWrapper {

	FileUploadProgress progress = new FileUploadProgress();
	private Map<String, String[]> params = new HashMap<String, String[]>();
	private Map<String, List<FileItem>> files = new HashMap<String, List<FileItem>>();

	public FileUploadRequestWrapper(int maxFileSize, HttpServletRequest request) {
		super(request);
		try {
			ServletFileUpload upload = new ServletFileUpload();
			upload.setFileSizeMax(maxFileSize);
			upload.setFileItemFactory(new DiskFileItemFactory());
			request.setAttribute("$files$", files);
			Map<String, List<String>> paramList = new HashMap<String, List<String>>();
			List<?> items = upload.parseRequest(request);
			Iterator<?> it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (item.isFormField()) {
					List<String> values = paramList.get(item.getFieldName());
					if (values == null) {
						values = new ArrayList<String>();
						paramList.put(item.getFieldName(), values);
					}
					values.add(item.getString("utf-8"));
				} else {
					List<FileItem> values = files.get(item.getFieldName());
					if (values == null) {
						values = new ArrayList<FileItem>();
						files.put(item.getFieldName(), values);
					}
					values.add(item);
				}
			}
			Iterator<String> it1 = paramList.keySet().iterator();
			while (it1.hasNext()) {
				String k = it1.next();
				List<String> ls = paramList.get(k);
				String[] v = new String[ls.size()];
				for (int i = 0; i < v.length; i++) {
					v[i] = ls.get(i);
				}
				this.params.put(k, v);
			}
		} catch (FileUploadException fe) {
		} catch (Exception ne) {
			throw new RuntimeException(ne);
		}
	}

	@Override
	public String getParameter(String name) {
		String[] r = params.get(name);
		if (r != null) {
			return r[0];
		} else {
			return null;
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
	public List<FileItem> getFileItemList(String name) {
		return files.get(name);
	}

	/**
	 * 取出第1个文件列表
	 * 
	 */
	public List<FileItem> getFirstFileItemList() {
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
	public Map<String, List<FileItem>> getFileItemListMap() {
		return files;
	}
}
