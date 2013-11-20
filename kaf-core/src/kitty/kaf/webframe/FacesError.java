package kitty.kaf.webframe;

public class FacesError extends Exception {
	private static final long serialVersionUID = -1349205802091929985L;
	String forward;

	public FacesError(String forward) {
		super();
		this.forward = forward;
	}

	public FacesError(String forward, String message, Throwable cause) {
		super(message, cause);
		this.forward = forward;
	}

	public FacesError(String forward, String message) {
		super(message);
		this.forward = forward;
	}

	public FacesError(String forward, Throwable cause) {
		super(cause);
		this.forward = forward;
	}

}
