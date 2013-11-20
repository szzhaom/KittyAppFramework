package kitty.testapp.inf.ds.file;

import kitty.kaf.cache.CacheValueList;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Remote;
import kitty.kaf.io.KeyValue;
import java.util.Date;
import kitty.testapp.inf.ds.file.beans.FileCategory;

@Remote
public interface FileCategoryBeanRemote {

    public void delete(Long loginUserId, List<Short> idList) throws SQLException;

    public FileCategory findById(Long loginUserId, Short id) throws SQLException;

    public FileCategory insert(Long loginUserId, FileCategory o) throws SQLException;

    public FileCategory edit(Long loginUserId, FileCategory o) throws SQLException;

    public List<FileCategory> query(Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException;

    public KeyValue<Integer, List<FileCategory>> queryPage(Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException;

    public Object execute(Long loginUserId, String cmd, List<?> params) throws SQLException;

    public CacheValueList<?, ?> queryLatest(Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws SQLException;
}
