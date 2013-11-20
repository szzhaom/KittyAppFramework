package kitty.kaf.json;

import java.io.Serializable;

public class NoQuoteString implements Serializable {
	private static final long serialVersionUID = 1L;
	String value;

	public NoQuoteString(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
