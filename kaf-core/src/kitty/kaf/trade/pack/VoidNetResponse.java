package kitty.kaf.trade.pack;

import java.util.Date;

/**
 * 空响应
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class VoidNetResponse implements NetResponse {
	private static final long serialVersionUID = 1L;
	Date recvTime;

	public VoidNetResponse() {
		super();
	}

	public VoidNetResponse(Date recvTime) {
		super();
		this.recvTime = recvTime;
	}

	@Override
	public void setRecvTime(Date value) {
		recvTime = value;
	}

	@Override
	public Date getRecvTime() {
		return recvTime;
	}

}
