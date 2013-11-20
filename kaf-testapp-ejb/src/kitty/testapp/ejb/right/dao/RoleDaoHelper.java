package kitty.testapp.ejb.right.dao;

import java.sql.SQLException;
import kitty.kaf.dao.Dao;
import java.util.List;
import kitty.kaf.io.KeyValue;
import kitty.kaf.cache.CacheValueList;
import java.util.Date;
import kitty.testapp.inf.ds.right.beans.Role;
import kitty.testapp.inf.ds.right.RoleHelper;
import kitty.kaf.helper.SQLHelper;

public class RoleDaoHelper {

    public static void delete(Dao dao, Long loginUserId, List<Integer> idList) throws SQLException {
        //[autogenerated:body(deleteCheck) statements=1]
        if (idList != null && idList.size() > 0) {
            try {
                dao.beginUpdateDatabase();
                if (dao.query(SQLHelper.buildSelectOrListSql("t_user_role", "role_id", idList), 1, idList).next()) throw new SQLException("删除角色失败：还有用户属于该角色，不能删除");
                dao.execute(SQLHelper.buildDeleteOrListSql("t_role_func", "role_id", idList));
                if (dao.query(SQLHelper.buildSelectOrListSql("t_user_owner_role", "role_id", idList), 1, idList).next()) throw new SQLException("删除角色失败：还有用户拥有该角色，不能删除");
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
        //[autogenerated:body(findById) statements=1]
        Role ret = dao.findById(Role.class, id);
        //[autogenerated:return(findById) statements=1]
        return ret;
    }

    public static Role insert(Dao dao, Long loginUserId, Role o) throws SQLException {
        //[autogenerated:body(insert) statements=1]
        Role ret = dao.insert(o);
        //[autogenerated:return(insert) statements=2]
        RoleHelper.roleMap.remove(o.getId());
        return ret;
    }

    public static Role edit(Dao dao, Long loginUserId, Role o) throws SQLException {
        //[autogenerated:body(edit) statements=1]
        Role ret = dao.edit(o);
        //[autogenerated:return(edit) statements=2]
        RoleHelper.roleMap.remove(o.getId());
        return ret;
    }

    public static List<Role> query(Dao dao, Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(query) statements=2]
        List<Role> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.query(Role.class, "from t_role a where a.is_delete=0", "", maxResults);
        //[autogenerated:return(query) statements=1]
        return ret;
    }

    public static KeyValue<Integer, List<Role>> queryPage(Dao dao, Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(queryPage) statements=2]
        KeyValue<Integer, List<Role>> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.queryPage(Role.class, "from t_role a where a.is_deleted=0", "", firstIndex, maxResults, params.get(0));
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
