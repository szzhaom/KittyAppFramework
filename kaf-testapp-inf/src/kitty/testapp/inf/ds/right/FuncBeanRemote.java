package kitty.testapp.inf.ds.right;

import kitty.kaf.cache.CacheValueList;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Remote;
import kitty.kaf.io.KeyValue;
import java.util.Date;
import kitty.testapp.inf.ds.right.beans.Func;

@Remote
public interface FuncBeanRemote {

    public void delete(Long loginUserId, List<Long> idList) throws SQLException;

    public Func findById(Long loginUserId, Long id) throws SQLException;

    public Func findByUniqueKey(Long loginUserId, String keyCode) throws SQLException;

    public Func insert(Long loginUserId, Func o) throws SQLException;

    public Func edit(Long loginUserId, Func o) throws SQLException;

    public List<Func> query(Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException;

    public KeyValue<Integer, List<Func>> queryPage(Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException;

    public Object execute(Long loginUserId, String cmd, List<?> params) throws SQLException;

    public CacheValueList<?, ?> queryLatest(Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws SQLException;
}
