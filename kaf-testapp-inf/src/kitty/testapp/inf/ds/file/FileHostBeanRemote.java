package kitty.testapp.inf.ds.file;

import kitty.kaf.cache.CacheValueList;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Remote;
import kitty.kaf.io.KeyValue;
import java.util.Date;
import kitty.testapp.inf.ds.file.beans.FileHost;

@Remote
public interface FileHostBeanRemote {

    public void delete(Long loginUserId, List<Short> idList) throws SQLException;

    public FileHost findById(Long loginUserId, Short id) throws SQLException;

    public FileHost insert(Long loginUserId, FileHost o) throws SQLException;

    public FileHost edit(Long loginUserId, FileHost o) throws SQLException;

    public List<FileHost> query(Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException;

    public KeyValue<Integer, List<FileHost>> queryPage(Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException;

    public Object execute(Long loginUserId, String cmd, List<?> params) throws SQLException;

    public CacheValueList<?, ?> queryLatest(Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws SQLException;
}
