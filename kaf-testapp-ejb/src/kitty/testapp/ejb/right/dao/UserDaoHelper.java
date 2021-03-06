package kitty.testapp.ejb.right.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import kitty.kaf.cache.CacheValueList;
import kitty.kaf.dao.Dao;
import kitty.kaf.dao.resultset.DaoResultSet;
import kitty.kaf.helper.SQLHelper;
import kitty.kaf.io.KeyValue;
import kitty.testapp.inf.ds.right.UserHelper;
import kitty.testapp.inf.ds.right.beans.User;

public class UserDaoHelper {

    public static void delete(Dao dao, Long loginUserId, List<Long> idList) throws SQLException {
        //[autogenerated:body(deleteCheck) statements=1]
        if (idList != null && idList.size() > 0) {
            try {
                dao.beginUpdateDatabase();
                dao.execute(SQLHelper.buildDeleteOrListSql("t_user_role", "user_id", idList));
                dao.execute(SQLHelper.buildDeleteOrListSql("t_user_owner_role", "user_id", idList));
            } finally {
                dao.endUpdateDatabase();
            }
        }
        //[autogenerated:body(delete) statements=1]
        dao.delete(User.tableDef, idList);
        //[autogenerated:body(deleteAfter) statements=1]
        UserHelper.userMap.removeAll(idList);
    }

    public static User findById(Dao dao, Long loginUserId, Long id) throws SQLException {
        //[autogenerated:body(findById) statements=2]
        User ret = dao.findById(User.class, id);
        if (ret != null) {
            DaoResultSet rset = dao.query(0, "select role_id from t_user_role where user_id=?", id);
            ret.setRoleIdList(rset.getIntList(0));
            rset = dao.query(0, "select role_id from t_user_owner_role where user_id=?", id);
            ret.setOwnerRoleIdList(rset.getIntList(0));
        }
        //[autogenerated:return(findById) statements=1]
        return ret;
    }

    public static User findByUniqueKey(Dao dao, Long loginUserId, String keyCode) throws SQLException {
        //[autogenerated:body(findByUniqueKey) statements=2]
        User ret = dao.findByUniqueKey(User.class, keyCode);
        if (ret != null) {
            DaoResultSet rset = dao.query(0, "select role_id from t_user_role where user_id=?", ret.getId());
            ret.setRoleIdList(rset.getIntList(0));
            rset = dao.query(0, "select role_id from t_user_owner_role where user_id=?", ret.getId());
            ret.setOwnerRoleIdList(rset.getIntList(0));
        }
        //[autogenerated:return(findByUniqueKey) statements=1]
        return ret;
    }

    public static User insert(Dao dao, Long loginUserId, User o) throws SQLException {
        //[autogenerated:body(insert) statements=3]
        User ret = dao.insert(o);
        if (o.getRoleIdList() != null) {
            for (Object id : o.getRoleIdList()) dao.execute("insert into t_user_role(user_id,role_id,last_modified_time,creation_time) values(?,?,${now},${now})", o.getId(), id);
        }
        if (o.getOwnerRoleIdList() != null) {
            for (Object id : o.getOwnerRoleIdList()) dao.execute("insert into t_user_owner_role(user_id,role_id,last_modified_time,creation_time) values(?,?,${now},${now})", o.getId(), id);
        }
        //[autogenerated:return(insert) statements=2]
        UserHelper.userMap.remove(o.getId());
        return ret;
    }

    public static User edit(Dao dao, Long loginUserId, User o) throws SQLException {
        //[autogenerated:body(edit) statements=3]
        User ret = dao.edit(o);
        if (o.getRoleIdList() != null) {
            dao.execute("delete from t_user_role where user_id=?", o.getId());
            for (Object id : o.getRoleIdList()) dao.execute("insert into t_user_role(user_id,role_id,last_modified_time,creation_time) values(?,?,${now},${now})", o.getId(), id);
        }
        if (o.getOwnerRoleIdList() != null) {
            dao.execute("delete from t_user_owner_role where user_id=?", o.getId());
            for (Object id : o.getOwnerRoleIdList()) dao.execute("insert into t_user_owner_role(user_id,role_id,last_modified_time,creation_time) values(?,?,${now},${now})", o.getId(), id);
        }
        //[autogenerated:return(edit) statements=2]
        UserHelper.userMap.remove(o.getId());
        return ret;
    }

    public static List<User> query(Dao dao, Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(query) statements=2]
        List<User> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.query(User.class, "from t_user a where a.is_deleted=0", "", maxResults);
        //[autogenerated:return(query) statements=1]
        return ret;
    }

    public static KeyValue<Integer, List<User>> queryPage(Dao dao, Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(queryPage) statements=2]
        KeyValue<Integer, List<User>> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.keywordQuery(User.class, "from t_user a where a.is_deleted=0", "", firstIndex, maxResults, params.get(0));
        else if (cmd.equalsIgnoreCase("query")) ret = dao.keywordQuery(User.class, "from t_user a where a.is_deleted=0 and user_id!=?", "", firstIndex, maxResults, params.get(0), loginUserId);
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
        if (cmd.equalsIgnoreCase("default")) ret = dao.queryLatest(User.class, "t_user a", "", "", firstIndex, maxResults, lastModified);
        //[autogenerated:return(queryLastest) statements=1]
        return ret;
    }
}
