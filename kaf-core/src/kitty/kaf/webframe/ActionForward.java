package kitty.kaf.webframe;

import java.io.Serializable;

public class ActionForward implements Serializable {
	private static final long serialVersionUID = 1L;
	private ActionForwardType forwardType;
	private String forwardValue;

	public ActionForward(ActionForwardType forwardType, String forwardValue) {
		super();
		this.forwardType = forwardType;
		this.forwardValue = forwardValue;
	}

	public ActionForwardType getForwardType() {
		return forwardType;
	}

	public void setForwardType(ActionForwardType forwardType) {
		this.forwardType = forwardType;
	}

	public String getForwardValue() {
		return forwardValue;
	}

	public void setForwardValue(String forwardValue) {
		this.forwardValue = forwardValue;
	}
}
