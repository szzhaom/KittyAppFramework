package kitty.kaf.trade.pack;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求包
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 */
public interface NetRequest extends Serializable {
	/**
	 * 设置请求发出的时间
	 */
	public void setSendTime(Date value);

	/**
	 * 获取请求发出的时间
	 * 
	 */
	public Date getSendTime();
}
