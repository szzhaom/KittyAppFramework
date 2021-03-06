package kitty.testapp.ejb.right.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import kitty.kaf.cache.CacheValueList;
import kitty.kaf.dao.Dao;
import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.helper.SQLHelper;
import kitty.kaf.io.KeyValue;
import kitty.testapp.inf.ds.right.RoleHelper;
import kitty.testapp.inf.ds.right.beans.Role;

public class RoleDaoHelper {

    public static void delete(Dao dao, Long loginUserId, List<Integer> idList) throws SQLException {
        //[autogenerated:body(deleteCheck) statements=1]
        if (idList != null && idList.size() > 0) {
            try {
                dao.beginUpdateDatabase();
                if (dao.query(1, SQLHelper.buildSelectOrListSql("t_user_role", "role_id", idList), idList).next()) throw new SQLException("还有用户属于该角色，不能删除");
                dao.execute(SQLHelper.buildDeleteOrListSql("t_role_func", "role_id", idList));
                if (dao.query(1, SQLHelper.buildSelectOrListSql("t_user_owner_role", "role_id", idList), idList).next()) throw new SQLException("还有用户拥有该角色，不能删除");
            } finally {
                dao.endUpdateDatabase();
            }
        }
        //[autogenerated:body(delete) statements=1]
        dao.delete(Role.tableDef, idList);
        //[autogenerated:body(deleteAfter) statements=1]
        RoleHelper.roleMap.removeAll(idList);
    }

    public static Role findById(Dao dao, Long loginUserId, Integer id) throws SQLException {
        //[autogenerated:body(findById) statements=2]
        Role ret = dao.findById(Role.class, id);
        if (ret != null) {
            DaoResultSet rset = dao.query(0, "select func_id from t_role_func where role_id=?", id);
            ret.setFuncIdList(rset.getLongList(0));
        }
        //[autogenerated:return(findById) statements=1]
        return ret;
    }

    public static Role insert(Dao dao, Long loginUserId, Role o) throws SQLException {
        //[autogenerated:body(insert) statements=2]
        Role ret = dao.insert(o);
        if (o.getFuncIdList() != null) {
            for (Object id : o.getFuncIdList()) dao.execute("insert into t_role_func(role_id,func_id,last_modified_time,creation_time) values(?,?,${now},${now})", o.getId(), id);
        }
        //[autogenerated:return(insert) statements=2]
        RoleHelper.roleMap.remove(o.getId());
        return ret;
    }

    public static Role edit(Dao dao, Long loginUserId, Role o) throws SQLException {
        //[autogenerated:body(edit) statements=2]
        Role ret = dao.edit(o);
        if (o.getFuncIdList() != null) {
            dao.execute("delete from t_role_func where role_id=?", o.getId());
            for (Object id : o.getFuncIdList()) dao.execute("insert into t_role_func(role_id,func_id,last_modified_time,creation_time) values(?,?,${now},${now})", o.getId(), id);
        }
        //[autogenerated:return(edit) statements=2]
        RoleHelper.roleMap.remove(o.getId());
        return ret;
    }

    public static List<Role> query(Dao dao, Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(query) statements=2]
        List<Role> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.query(Role.class, "from t_role a where a.is_deleted=0", "", maxResults);
        //[autogenerated:return(query) statements=1]
        return ret;
    }

    public static KeyValue<Integer, List<Role>> queryPage(Dao dao, Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(queryPage) statements=2]
        KeyValue<Integer, List<Role>> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.keywordQuery(Role.class, "from t_role a where a.is_deleted=0", "", firstIndex, maxResults, params.get(0));
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
        if (cmd.equalsIgnoreCase("default")) ret = dao.queryLatest(Role.class, "t_role a", "", "", firstIndex, maxResults, lastModified);
        //[autogenerated:return(queryLastest) statements=1]
        return ret;
    }
}
