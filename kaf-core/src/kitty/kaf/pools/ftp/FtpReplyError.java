package kitty.kaf.pools.ftp;

import java.io.IOException;

public class FtpReplyError extends IOException {
	private static final long serialVersionUID = 1L;
	private int replyCode;

	public FtpReplyError(int replyCode) {
		super();
		this.replyCode = replyCode;
	}

	public FtpReplyError(int replyCode, String message, Throwable cause) {
		super(message, cause);
		this.replyCode = replyCode;
	}

	public FtpReplyError(int replyCode, String message) {
		super(message);
		this.replyCode = replyCode;
	}

	public FtpReplyError(int replyCode, Throwable cause) {
		super(cause);
		this.replyCode = replyCode;
	}

	public int getReplyCode() {
		return replyCode;
	}

}
