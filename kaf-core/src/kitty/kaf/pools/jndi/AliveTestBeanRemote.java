package kitty.kaf.pools.jndi;

import javax.ejb.Remote;

/**
 * 
 * @author 赵明
 */
@Remote
public interface AliveTestBeanRemote {
	public String test(String msg);

	public boolean isOk(String source);
}
