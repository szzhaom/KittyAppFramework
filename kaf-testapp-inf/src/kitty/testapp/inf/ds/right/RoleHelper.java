package kitty.testapp.inf.ds.right;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kitty.kaf.pools.jndi.JndiConnectionFactory;
import kitty.kaf.session.AbstractRequestSession;
import kitty.kaf.session.RequestSession;
import java.util.List;
import java.util.Date;
import kitty.kaf.io.KeyValue;
import kitty.kaf.pools.jndi.Lookuper;
import kitty.testapp.inf.ds.right.beans.Role;
import kitty.kaf.cache.CacheValueList;
import kitty.kaf.pools.memcached.MemcachedClient;
import kitty.kaf.cache.MemcachedCallback;
import kitty.kaf.pools.memcached.MemcachedMap;

public class RoleHelper {

    //[autogenerated:static(cached) statements=2]
    public static final MemcachedClient mc = MemcachedClient.newInstance(null, "default");

    public static final MemcachedMap<Integer, Role> roleMap = new MemcachedMap<Integer, Role>(new MemcachedCallback() {

        @Override
        public Object onGetCacheValue(Object source, Object id) throws Throwable {
            Role ret = null;
            if (id instanceof Integer) ret = findById(null, null, (Integer) id);
            getCacheValueCompete(ret);
            return ret;
        }

        @Override
        public boolean isNullId(Object v) {
            return ((Integer) v).compareTo(-1) <= 0;
        }
    }, mc, Role.CACHE_KEY_PREFIX, Role.class);

    public static void delete(Object caller, Long loginUserId, List<Integer> idList) throws Exception {
        //[autogenerated:return(delete) statements=2]
        RoleBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappRoleBean", RoleBeanRemote.class);
        bean.delete(loginUserId, idList);
    }

    public static Role findById(Object caller, Long loginUserId, Integer id) throws Exception {
        //[autogenerated:return(findById) statements=2]
        RoleBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappRoleBean", RoleBeanRemote.class);
        return bean.findById(loginUserId, id);
    }

    public static Role insert(Object caller, Long loginUserId, Role o) throws Exception {
        //[autogenerated:return(insert) statements=2]
        RoleBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappRoleBean", RoleBeanRemote.class);
        return bean.insert(loginUserId, o);
    }

    public static Role edit(Object caller, Long loginUserId, Role o) throws Exception {
        //[autogenerated:return(edit) statements=2]
        RoleBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappRoleBean", RoleBeanRemote.class);
        return bean.edit(loginUserId, o);
    }

    public static List<Role> query(Object caller, Long loginUserId, String cmd, int maxResults, List<?> params) throws Exception {
        //[autogenerated:return(query) statements=2]
        RoleBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappRoleBean", RoleBeanRemote.class);
        return bean.query(loginUserId, cmd, maxResults, params);
    }

    public static CacheValueList<?, ?> queryLatest(Object caller, Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws Exception {
        //[autogenerated:return(queryLatest) statements=2]
        RoleBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappRoleBean", RoleBeanRemote.class);
        return bean.queryLatest(loginUserId, cmd, firstIndex, maxResults, lastModified);
    }

    public static KeyValue<Integer, List<Role>> queryPage(Object caller, Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws Exception {
        //[autogenerated:return(queryPage) statements=2]
        RoleBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappRoleBean", RoleBeanRemote.class);
        return bean.queryPage(loginUserId, cmd, firstIndex, maxResults, params);
    }

    public static Object execute(Object caller, Long loginUserId, String cmd, List<?> params) throws Exception {
        //[autogenerated:return(execute) statements=2]
        RoleBeanRemote bean = JndiConnectionFactory.lookup("db", caller, Lookuper.JNDI_TYPE_EJB, "testappRoleBean", RoleBeanRemote.class);
        return bean.execute(loginUserId, cmd, params);
    }

    public static void getCacheValueCompete(Role v) {
    }

    public static void insertOrEditPageProcess(HttpServletRequest request, HttpServletResponse response) {
        //[autogenerated:body(insertOrEditPageProcess) statements=4]
        String id = request.getParameter("id");
        String error = null;
        if (id != null) {
            try {
                RequestSession<?> session = AbstractRequestSession.getCurrentSession(request);
                Role p = RoleHelper.findById(null, session.getUser().getUserId(), Integer.valueOf(id));
                if (p == null) error = "找不到角色信息[id=" + id + "]";
                else request.setAttribute("data", p);
            } catch (Throwable e) {
                error = e.getMessage();
            }
        }
        else request.setAttribute("data", new Role());
        if (error != null) request.setAttribute("error", error);
    }
}