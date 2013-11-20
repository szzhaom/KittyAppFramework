package kitty.testapp.ejb.right.dao;

import java.sql.SQLException;
import kitty.kaf.dao.Dao;
import java.util.List;
import kitty.kaf.io.KeyValue;
import kitty.kaf.cache.CacheValueList;
import java.util.Date;
import kitty.testapp.inf.ds.right.beans.Func;
import kitty.testapp.inf.ds.right.FuncHelper;

public class FuncDaoHelper {

    public static void delete(Dao dao, Long loginUserId, List<Long> idList) throws SQLException {
        //[autogenerated:body(delete) statements=1]
        dao.delete(Func.tableDef, idList);
        //[autogenerated:body(deleteAfter) statements=1]
        FuncHelper.localFuncMap.setSourceLastModified(new Date());
    }

    public static Func findById(Dao dao, Long loginUserId, Long id) throws SQLException {
        //[autogenerated:body(findById) statements=1]
        Func ret = dao.findById(Func.class, id);
        //[autogenerated:return(findById) statements=1]
        return ret;
    }

    public static Func findByUniqueKey(Dao dao, Long loginUserId, String keyCode) throws SQLException {
        //[autogenerated:body(findByUniqueKey) statements=1]
        Func ret = dao.findByUniqueKey(Func.class, keyCode);
        //[autogenerated:return(findByUniqueKey) statements=1]
        return ret;
    }

    public static Func insert(Dao dao, Long loginUserId, Func o) throws SQLException {
        //[autogenerated:body(insert) statements=1]
        Func ret = dao.insert(o);
        //[autogenerated:return(insert) statements=2]
        FuncHelper.localFuncMap.setSourceLastModified(new Date());
        return ret;
    }

    public static Func edit(Dao dao, Long loginUserId, Func o) throws SQLException {
        //[autogenerated:body(edit) statements=1]
        Func ret = dao.edit(o);
        //[autogenerated:return(edit) statements=2]
        FuncHelper.localFuncMap.setSourceLastModified(new Date());
        return ret;
    }

    public static List<Func> query(Dao dao, Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(query) statements=2]
        List<Func> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.query(Func.class, "from t_func a where a.is_delete=0", "", maxResults);
        //[autogenerated:return(query) statements=1]
        return ret;
    }

    public static KeyValue<Integer, List<Func>> queryPage(Dao dao, Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(queryPage) statements=2]
        KeyValue<Integer, List<Func>> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.queryPage(Func.class, "from t_func a where a.is_deleted=0", "", firstIndex, maxResults, params.get(0));
        //[autogenerated:return(queryPage) statements=1]
        return ret;
    }

    public static Object execute(Dao dao, Long loginUserId, String cmd, List<?> params) throws SQLException {
        //[autogenerated:body(execute) statements=1]
        Object ret = null;
        //[autogenerated:return(execute) statements=1]
        return ret;
    }

    public static CacheValueList<?, ?> queryLatest(Dao dao, Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws SQLException {
        //[autogenerated:body(queryLastest) statements=2]
        CacheValueList<?, ?> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.queryLatest(Func.class, "t_func a", "", "", firstIndex, maxResults, lastModified);
        //[autogenerated:return(queryLastest) statements=1]
        return ret;
    }
}
