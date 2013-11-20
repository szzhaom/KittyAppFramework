package kitty.kaf.trade.pack;

import java.io.Serializable;
import java.util.Date;

/**
 * 响应包
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public interface NetResponse extends Serializable {
	/**
	 * 设置响应收到的时间
	 */
	public void setRecvTime(Date value);

	/**
	 * 获取响应收到的时间
	 * 
	 */
	public Date getRecvTime();
}
