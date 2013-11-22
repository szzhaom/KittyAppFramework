package kitty.testapp.ejb.file.dao;

import java.sql.SQLException;
import kitty.kaf.dao.Dao;
import java.util.List;
import kitty.kaf.io.KeyValue;
import kitty.kaf.cache.CacheValueList;
import java.util.Date;
import kitty.testapp.inf.ds.file.beans.FileCategory;
import kitty.testapp.inf.ds.file.FileCategoryHelper;

public class FileCategoryDaoHelper {

    public static void delete(Dao dao, Long loginUserId, List<Short> idList) throws SQLException {
        //[autogenerated:body(delete) statements=1]
        dao.delete(FileCategory.tableDef, idList);
        //[autogenerated:body(deleteAfter) statements=1]
        FileCategoryHelper.localFileCategoryMap.setSourceLastModified(new Date());
    }

    public static FileCategory findById(Dao dao, Long loginUserId, Short id) throws SQLException {
        //[autogenerated:body(findById) statements=1]
        FileCategory ret = dao.findById(FileCategory.class, id);
        //[autogenerated:return(findById) statements=1]
        return ret;
    }

    public static FileCategory insert(Dao dao, Long loginUserId, FileCategory o) throws SQLException {
        //[autogenerated:body(insert) statements=1]
        FileCategory ret = dao.insert(o);
        //[autogenerated:return(insert) statements=2]
        FileCategoryHelper.localFileCategoryMap.setSourceLastModified(new Date());
        return ret;
    }

    public static FileCategory edit(Dao dao, Long loginUserId, FileCategory o) throws SQLException {
        //[autogenerated:body(edit) statements=1]
        FileCategory ret = dao.edit(o);
        //[autogenerated:return(edit) statements=2]
        FileCategoryHelper.localFileCategoryMap.setSourceLastModified(new Date());
        return ret;
    }

    public static List<FileCategory> query(Dao dao, Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(query) statements=2]
        List<FileCategory> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.query(FileCategory.class, "from t_file_category a where a.is_delete=0", "", maxResults);
        //[autogenerated:return(query) statements=1]
        return ret;
    }

    public static KeyValue<Integer, List<FileCategory>> queryPage(Dao dao, Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(queryPage) statements=2]
        KeyValue<Integer, List<FileCategory>> ret = null;
        if (cmd.equalsIgnoreCase("default")) ret = dao.keywordQuery(FileCategory.class, "from t_file_category a where a.is_deleted=0", "", firstIndex, maxResults, params.get(0));
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
        if (cmd.equalsIgnoreCase("default")) ret = dao.queryLatest(FileCategory.class, "t_file_category a", "", "", firstIndex, maxResults, lastModified);
        //[autogenerated:return(queryLastest) statements=1]
        return ret;
    }
}
