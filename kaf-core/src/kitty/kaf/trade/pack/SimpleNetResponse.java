package kitty.kaf.trade.pack;

import java.util.Date;

/**
 * 包含有简单数据的响应包
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class SimpleNetResponse implements NetResponse {
	private static final long serialVersionUID = 1L;
	Date recvTime;
	Object value;

	public SimpleNetResponse() {
		super();
	}

	/**
	 * 创建一个简单的响应包
	 * 
	 * @param value
	 *            响应数据
	 * @param recvTime
	 *            响应数据
	 */
	public SimpleNetResponse(Object value, Date recvTime) {
		super();
		this.recvTime = recvTime;
		this.value = value;
	}

	@Override
	public void setRecvTime(Date value) {
		recvTime = value;
	}

	@Override
	public Date getRecvTime() {
		return recvTime;
	}

	/**
	 * 获取响应数据
	 * 
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * 设置响应数据
	 * 
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}
