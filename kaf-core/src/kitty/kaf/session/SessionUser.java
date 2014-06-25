package kitty.kaf.session;


/**
 * 会话用户
 * 
 * @author 赵明
 * 
 */
public interface SessionUser {
	/**
	 * 获取会话用户ID
	 */
	public Long getUserId();

	/**
	 * 获取会话用户登录名
	 */
	public String getLoginName();

	/**
	 * 检查用户是否具备某个权限
	 * 
	 * @param right
	 *            权限ID
	 * @return 是否具备该权限
	 */
	public boolean hasRight(long right);

}
