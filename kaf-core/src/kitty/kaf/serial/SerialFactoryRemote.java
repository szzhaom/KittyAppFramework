package kitty.kaf.serial;

import javax.ejb.Remote;

@Remote
public interface SerialFactoryRemote {
	public String getNextSerial(String key);
}
