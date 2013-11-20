package kitty.testapp.ejb.file;

import java.sql.SQLException;
import java.util.List;
import javax.ejb.Stateless;
import kitty.kaf.io.KeyValue;
import kitty.kaf.cache.CacheValueList;
import java.util.Date;
import kitty.kaf.dao.DaoBean;
import kitty.testapp.ejb.file.dao.FileCategoryDaoHelper;
import kitty.testapp.inf.ds.file.beans.FileCategory;
import kitty.testapp.inf.ds.file.FileCategoryBeanRemote;

@Stateless(name = "testappFileCategoryBean", mappedName = "testappFileCategoryBean")
public class FileCategoryBean extends DaoBean implements FileCategoryBeanRemote {

    public void delete(Long loginUserId, List<Short> idList) throws SQLException {
        //[autogenerated:body(delete) statements=1]
        FileCategoryDaoHelper.delete(getDao(), loginUserId, idList);
    }

    public FileCategory findById(Long loginUserId, Short id) throws SQLException {
        //[autogenerated:body(findById) statements=1]
        FileCategory ret = FileCategoryDaoHelper.findById(getDao(), loginUserId, id);
        //[autogenerated:return(findById) statements=1]
        return ret;
    }

    public FileCategory insert(Long loginUserId, FileCategory o) throws SQLException {
        //[autogenerated:body(insert) statements=1]
        FileCategory ret = FileCategoryDaoHelper.insert(getDao(), loginUserId, o);
        //[autogenerated:return(insert) statements=1]
        return ret;
    }

    public FileCategory edit(Long loginUserId, FileCategory o) throws SQLException {
        //[autogenerated:body(edit) statements=1]
        FileCategory ret = FileCategoryDaoHelper.edit(getDao(), loginUserId, o);
        //[autogenerated:return(edit) statements=1]
        return ret;
    }

    public List<FileCategory> query(Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(query) statements=1]
        List<FileCategory> ret = FileCategoryDaoHelper.query(getDao(), loginUserId, cmd, maxResults, params);
        //[autogenerated:return(query) statements=1]
        return ret;
    }

    public KeyValue<Integer, List<FileCategory>> queryPage(Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException {
        //[autogenerated:body(queryPage) statements=1]
        KeyValue<Integer, List<FileCategory>> ret = FileCategoryDaoHelper.queryPage(getDao(), loginUserId, cmd, firstIndex, maxResults, params);
        //[autogenerated:return(queryPage) statements=1]
        return ret;
    }

    public CacheValueList<?, ?> queryLatest(Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws SQLException {
        //[autogenerated:body(queryLatest) statements=1]
        CacheValueList<?, ?> ret = FileCategoryDaoHelper.queryLatest(getDao(), loginUserId, cmd, firstIndex, maxResults, lastModified);
        //[autogenerated:return(queryLatest) statements=1]
        return ret;
    }

    public Object execute(Long loginUserId, String cmd, List<?> params) throws SQLException {
        //[autogenerated:body(execute) statements=1]
        Object ret = FileCategoryDaoHelper.execute(getDao(), loginUserId, cmd, params);
        //[autogenerated:return(execute) statements=1]
        return ret;
    }
}
