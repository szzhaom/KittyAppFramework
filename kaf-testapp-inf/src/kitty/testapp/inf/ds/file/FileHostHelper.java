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
import kitty.testapp.inf.ds.file.beans.FileHost;
import kitty.kaf.cache.CacheValueList;
import kitty.kaf.listeners.ItemChangedEventListener;
import kitty.kaf.pools.memcached.MemcachedClient;
import kitty.kaf.cache.LocalCacheCallback;
import kitty.kaf.cache.LocalCachedMap;

public class FileHostHelper {

    //[autogenerated:static(cached) statements=4]
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
        public CacheValueList<?, ?> onGetCacheValueList(Object source, long firstIndex, int maxResults, Date lastModified) throws Throwable {
            return FileHostHelper.queryLatest(null, null, "default", firstIndex, maxResults, lastModified);
        }

        public boolean isNullId(Object v) {
            return ((Short) v).compareTo((short)-1) <= 0;
        }
    };

    public static LocalCachedMap<Short, FileHost> localFileHostMap = new LocalCachedMap<Short, FileHost>("filehost", mc, localCacheCallBack, itemsChangedEventListener, 1);

    static void localCacheChanged() throws Throwable {
    }

    static void localCacheRemoved(Object item) throws Throwable {
    }

    static void localCacheEdited(Object item, Object newValue) throws Throwable {
    }

    static void localCacheAdded(Object item) throws Throwable {
    }

    public static void delete(Object caller, Long loginUserId, List<Short> idList) throws Exception {
        //[autogenerated:return(delete) statements=2]
        FileHostBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappFileHostBean", FileHostBeanRemote.class);
        bean.delete(loginUserId, idList);
    }

    public static FileHost findById(Object caller, Long loginUserId, Short id) throws Exception {
        //[autogenerated:return(findById) statements=2]
        FileHostBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappFileHostBean", FileHostBeanRemote.class);
        return bean.findById(loginUserId, id);
    }

    public static FileHost insert(Object caller, Long loginUserId, FileHost o) throws Exception {
        //[autogenerated:return(insert) statements=2]
        FileHostBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappFileHostBean", FileHostBeanRemote.class);
        return bean.insert(loginUserId, o);
    }

    public static FileHost edit(Object caller, Long loginUserId, FileHost o) throws Exception {
        //[autogenerated:return(edit) statements=2]
        FileHostBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappFileHostBean", FileHostBeanRemote.class);
        return bean.edit(loginUserId, o);
    }

    public static List<FileHost> query(Object caller, Long loginUserId, String cmd, int maxResults, List<?> params) throws Exception {
        //[autogenerated:return(query) statements=2]
        FileHostBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappFileHostBean", FileHostBeanRemote.class);
        return bean.query(loginUserId, cmd, maxResults, params);
    }

    public static CacheValueList<?, ?> queryLatest(Object caller, Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws Exception {
        //[autogenerated:return(queryLatest) statements=2]
        FileHostBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappFileHostBean", FileHostBeanRemote.class);
        return bean.queryLatest(loginUserId, cmd, firstIndex, maxResults, lastModified);
    }

    public static KeyValue<Integer, List<FileHost>> queryPage(Object caller, Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws Exception {
        //[autogenerated:return(queryPage) statements=2]
        FileHostBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappFileHostBean", FileHostBeanRemote.class);
        return bean.queryPage(loginUserId, cmd, firstIndex, maxResults, params);
    }

    public static Object execute(Object caller, Long loginUserId, String cmd, List<?> params) throws Exception {
        //[autogenerated:return(execute) statements=2]
        FileHostBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappFileHostBean", FileHostBeanRemote.class);
        return bean.execute(loginUserId, cmd, params);
    }

    public static void getCacheValueCompete(FileHost v) {
    }

    public static void insertOrEditPageProcess(HttpServletRequest request, HttpServletResponse response) {
        //[autogenerated:body(insertOrEditPageProcess) statements=4]
        String id = request.getParameter("id");
        String error = null;
        if (id != null) {
            try {
                RequestSession<?> session = AbstractRequestSession.getCurrentSession(request);
                FileHost p = FileHostHelper.findById(null, session.getUser().getUserId(), Short.valueOf(id));
                if (p == null) error = "找不到文件主机信息[id=" + id + "]";
                else request.setAttribute("data", p);
            } catch (Throwable e) {
                error = e.getMessage();
            }
        }
        else request.setAttribute("data", new FileHost());
        if (error != null) request.setAttribute("error", error);
    }
}