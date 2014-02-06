package kitty.kaf.serial;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;

import kitty.kaf.KafUtil;
import kitty.kaf.dao.Dao;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.util.DateTime;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SerialFactory {
	static KafLogger logger = KafLogger.getLogger(SerialFactory.class);

	public static abstract class SerialConfig {
		abstract public long getNextSerial(Dao dao) throws SQLException;
	}

	public static class DaySerialConfig extends SerialConfig {
		String key;
		String mappedKey;
		long multiple;
		long startValue, maxValue;
		long currentValue = 0;
		long day = 0;
		long nextUpdateValue = 0;
		int cacheStep;

		public synchronized long getNextSerial(Dao dao) throws SQLException {
			int today = new DateTime().getFullDay();
			String dayKey = mappedKey + "_" + today;
			if (today != day) {
				Object r = dao.query(1, "select serial_value from t_serial where serial_key=?", dayKey);
				if (r != null) {
					currentValue = ((Number) r).longValue();
				} else
					currentValue = startValue;
				nextUpdateValue = currentValue + cacheStep;
				dao.beginUpdateDatabase();
				try {
					dao.execute("insert into t_serial(serial_key,serial_value) values(?,?)", dayKey, nextUpdateValue);
				} finally {
					dao.endUpdateDatabase();
				}
			} else {
				currentValue++;
				if (currentValue >= nextUpdateValue) {
					nextUpdateValue = currentValue + cacheStep;
					dao.beginUpdateDatabase();
					try {
						dao.execute("update t_serial set serial_value=? where serial_key=?", nextUpdateValue, dayKey);
					} finally {
						dao.endUpdateDatabase();
					}
				}
			}
			return today * multiple + currentValue;
		}
	}

	static ConcurrentHashMap<String, SerialConfig> daySerialConfigMap = new ConcurrentHashMap<String, SerialFactory.SerialConfig>();
	static {
		try {
			init();
		} catch (Throwable e) {
			logger.debug("init serial factory error:", e);
		}
	}

	static void init() throws ParserConfigurationException, SAXException, IOException {
		Element root = KafUtil.getBasicConfigRoot();
		NodeList ls = root.getElementsByTagName("serial-config");
		for (int i = 0; i < ls.getLength(); i++) {
			Element el = (Element) ls.item(i);
			String k = el.getAttribute("key");
			DaySerialConfig c = new DaySerialConfig();
			c.key = k;
			c.mappedKey = el.getAttribute("mapped_key");
			c.startValue = Long.valueOf(el.getAttribute("start"));
			c.maxValue = Long.valueOf(el.getAttribute("max"));
			c.multiple = (long) Math.pow(10, Integer.valueOf(el.getAttribute("length")));
			c.cacheStep = Integer.valueOf(el.getAttribute("cacheStep"));
			daySerialConfigMap.put(k, c);
		}
	}

	public SerialFactory() {
	}

	public static String getNextDaySerial(Dao dao, String key) throws SQLException {
		SerialConfig config = daySerialConfigMap.get(key);
		if (config == null)
			throw new SQLException("serial[" + key + "] not config.");
		return Long.toString(config.getNextSerial(dao));
	}
}
