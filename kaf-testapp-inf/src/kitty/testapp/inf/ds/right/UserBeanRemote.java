package kitty.testapp.inf.ds.right;

import kitty.kaf.cache.CacheValueList;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Remote;
import kitty.kaf.io.KeyValue;
import java.util.Date;
import kitty.testapp.inf.ds.right.beans.User;

@Remote
public interface UserBeanRemote {

    public void delete(Long loginUserId, List<Long> idList) throws SQLException;

    public User findById(Long loginUserId, Long id) throws SQLException;

    public User findByUniqueKey(Long loginUserId, String keyCode) throws SQLException;

    public User insert(Long loginUserId, User o) throws SQLException;

    public User edit(Long loginUserId, User o) throws SQLException;

    public List<User> query(Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException;

    public KeyValue<Integer, List<User>> queryPage(Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException;

    public Object execute(Long loginUserId, String cmd, List<?> params) throws SQLException;

    public CacheValueList<?, ?> queryLatest(Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws SQLException;
}
