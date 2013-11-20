package kitty.testapp.inf.ds.right;

import kitty.kaf.cache.CacheValueList;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Remote;
import kitty.kaf.io.KeyValue;
import java.util.Date;
import kitty.testapp.inf.ds.right.beans.Role;

@Remote
public interface RoleBeanRemote {

    public void delete(Long loginUserId, List<Integer> idList) throws SQLException;

    public Role findById(Long loginUserId, Integer id) throws SQLException;

    public Role insert(Long loginUserId, Role o) throws SQLException;

    public Role edit(Long loginUserId, Role o) throws SQLException;

    public List<Role> query(Long loginUserId, String cmd, int maxResults, List<?> params) throws SQLException;

    public KeyValue<Integer, List<Role>> queryPage(Long loginUserId, String cmd, long firstIndex, int maxResults, List<?> params) throws SQLException;

    public Object execute(Long loginUserId, String cmd, List<?> params) throws SQLException;

    public CacheValueList<?, ?> queryLatest(Long loginUserId, String cmd, long firstIndex, int maxResults, Date lastModified) throws SQLException;
}
