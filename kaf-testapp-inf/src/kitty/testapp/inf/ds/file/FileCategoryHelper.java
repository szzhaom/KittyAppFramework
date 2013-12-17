package kitty.testapp.inf.ds.file;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kitty.kaf.pools.jndi.JndiConnectionFactory;
import kitty.kaf.session.AbstractRequestSession;
import kitty.kaf.session.RequestSession;
import java.util.List;
import java.util.Date;
import kitty.kaf.io.KeyValue;
import kitty.kaf.pools.jndi.Lookuper;
import kitty.testapp.inf.ds.file.beans.FileCategory;
import kitty.kaf.cache.CacheValueList;
import kitty.kaf.listeners.ItemChangedEventListener;
import kitty.kaf.pools.memcached.MemcachedClient;
import kitty.kaf.cache.LocalCacheCallback;
import kitty.kaf.cache.LocalCachedMap;
import java.util.ArrayList;

public class FileCategoryHelper {

	// [autogenerated:static(cached) statements=4]
	public static final MemcachedClient mc = MemcachedClient.newInstance(null, "default");

	static ItemChangedEventListener itemsChangedEventListener = new ItemChangedEventListener() {

		@Override
		public void change(Object sender) throws Throwable {
			localCacheChanged();
		}

		@Override
		public void remove(Object sender, Object item) throws Throwable {
			localCacheRemoved(item);
		}

		@Override
		public void edit(Object sender, Object item, Object newValue) throws Throwable {
			localCacheEdited(item, newValue);
		}

		@Override
		public void add(Object sender, Object item) throws Throwable {
			localCacheAdded(item);
		}
	};

	static LocalCacheCallback localCacheCallBack = new LocalCacheCallback() {

		@Override
		public CacheValueList<?, ?> onGetCacheValueList(Object source, long firstIndex, int maxResults,
				Date lastModified) throws Throwable {
			return FileCategoryHelper.queryLatest(null, null, "default", firstIndex, maxResults, lastModified);
		}

		public boolean isNullId(Object v) {
			return ((Short) v).compareTo((short) -1) <= 0;
		}
	};

	public static LocalCachedMap<Short, FileCategory> localFileCategoryMap = new LocalCachedMap<Short, FileCategory>(
			"filecategory", mc, localCacheCallBack, itemsChangedEventListener, 1);

	static void localCacheChanged() throws Throwable {
	}

	static void localCacheRemoved(Object item) throws Throwable {
	}

	static void localCacheEdited(Object item, Object newValue) throws Throwable {
	}

	static void localCacheAdded(Object item) throws Throwable {
	}

	public static void delete(Object caller, Long loginUserId, List<Short> idList) throws Exception {
		// [autogenerated:begin(delete) statements=1]
		FileCategoryBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB,
				"testappFileCategoryBean", FileCategoryBeanRemote.class);
		// [autogenerated:body(delete) statements=1]
		bean.delete(loginUserId, idList);
	}

	public static FileCategory findById(Object caller, Long loginUserId, Short id) throws Exception {
		// [autogenerated:begin(findById) statements=1]
		FileCategoryBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB,
				"testappFileCategoryBean", FileCategoryBeanRemote.class);
		// [autogenerated:body(findById) statements=1]
		FileCategory ret = bean.findById(loginUserId, id);
		// [autogenerated:return(findById) statements=1]
		return ret;
	}

	public static FileCategory insert(Object caller, Long loginUserId, FileCategory o) throws Exception {
		// [autogenerated:begin(insert) statements=1]
		FileCategoryBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB,
				"testappFileCategoryBean", FileCategoryBeanRemote.class);
		// [autogenerated:body(insert) statements=1]
		FileCategory ret = bean.insert(loginUserId, o);
		// [autogenerated:return(insert) statements=1]
		return ret;
	}

	public static FileCategory edit(Object caller, Long loginUserId, FileCategory o) throws Exception {
		// [autogenerated:begin(edit) statements=1]
		FileCategoryBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB,
				"testappFileCategoryBean", FileCategoryBeanRemote.class);
		// [autogenerated:body(edit) statements=1]
		FileCategory ret = bean.edit(loginUserId, o);
		// [autogenerated:return(edit) statements=1]
		return ret;
	}

	public static List<FileCategory> query(Object caller, Long loginUserId, String cmd, int maxResults, List<?> params)
			throws Exception {
		// [autogenerated:begin(query) statements=1]
		FileCategoryBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB,
				"testappFileCategoryBean", FileCategoryBeanRemote.class);
		// [autogenerated:body(query) statements=1]
		List<FileCategory> ret = bean.query(loginUserId, cmd, maxResults, params);
		// [autogenerated:return(query) statements=1]
		return ret;
	}

	public static CacheValueList<?, ?> queryLatest(Object caller, Long loginUserId, String cmd, long firstIndex,
			int maxResults, Date lastModified) throws Exception {
		// [autogenerated:begin(queryLatest) statements=1]
		FileCategoryBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB,
				"testappFileCategoryBean", FileCategoryBeanRemote.class);
		// [autogenerated:body(queryLatest) statements=1]
		CacheValueList<?, ?> ret = bean.queryLatest(loginUserId, cmd, firstIndex, maxResults, lastModified);
		// [autogenerated:return(queryLatest) statements=1]
		return ret;
	}

	public static KeyValue<Integer, List<FileCategory>> queryPage(Object caller, Long loginUserId, String cmd,
			long firstIndex, int maxResults, List<?> params) throws Exception {
		// [autogenerated:begin(queryPage) statements=1]
		FileCategoryBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB,
				"testappFileCategoryBean", FileCategoryBeanRemote.class);
		// [autogenerated:body(queryPage) statements=1]
		KeyValue<Integer, List<FileCategory>> ret = bean.queryPage(loginUserId, cmd, firstIndex, maxResults, params);
		// [autogenerated:return(queryPage) statements=1]
		return ret;
	}

	public static Object execute(Object caller, Long loginUserId, String cmd, List<?> params) throws Exception {
		// [autogenerated:begin(execute) statements=1]
		FileCategoryBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB,
				"testappFileCategoryBean", FileCategoryBeanRemote.class);
		// [autogenerated:body(execute) statements=1]
		Object ret = bean.execute(loginUserId, cmd, params);
		// [autogenerated:return(execute) statements=1]
		return ret;
	}

	public static void getCacheValueCompete(FileCategory v) {
	}

	public static void insertOrEditPageProcess(HttpServletRequest request, HttpServletResponse response) {
		// [autogenerated:body(insertOrEditPageProcess) statements=4]
		String id = request.getParameter("id");
		String error = null;
		if (id != null) {
			try {
				RequestSession<?> session = AbstractRequestSession.getCurrentSession(request);
				FileCategory p = FileCategoryHelper.findById(null, session.getUser().getUserId(), Short.valueOf(id));
				if (p == null)
					error = "找不到文件分类信息[id=" + id + "]";
				else
					request.setAttribute("data", p);
			} catch (Throwable e) {
				error = e.getMessage();
			}
		} else
			request.setAttribute("data", new FileCategory());
		if (error != null)
			request.setAttribute("error", error);
	}

	public static Object execute(Object caller, Long loginUserId, String cmd, Object... params) throws Exception {
		// [autogenerated:return(execute...) statements=3]
		List<Object> ls = new ArrayList<Object>();
		for (Object o : params)
			ls.add(o);
		return execute(caller, loginUserId, cmd, ls);
	}
}
