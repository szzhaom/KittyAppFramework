package kitty.kaf.helper;

public class LocationHelper {
	/**
	 * 每经度距离，单位：米
	 */
	final public static double MI_PER_LONGITUDE = 85276.0;
	/**
	 * 每纬度距离，单位：米
	 */
	final public static double MI_PER_LATITUDE = 110940.0;
	/**
	 * 500M范围之内的经度偏移
	 */
	final public static double OFFSET_500M_LONGITUDE = 250.0 / MI_PER_LONGITUDE;
	/**
	 * 500M范围之内的纬度偏移
	 */
	final public static double OFFSET_500M_LATITUDE = 250.0 / MI_PER_LATITUDE;
	/**
	 * 1000M范围之内的纬度偏移
	 */
	final public static double OFFSET_1000M_LATITUDE = 500.0 / MI_PER_LATITUDE;
	/**
	 * 1000M范围之内的经度偏移
	 */
	final public static double OFFSET_1000M_LONGITUDE = 500.0 / MI_PER_LONGITUDE;
	/**
	 * 2000M范围之内的纬度偏移
	 */
	final public static double OFFSET_2000M_LATITUDE = 1000.0 / MI_PER_LATITUDE;
	/**
	 * 2000M范围之内的经度偏移
	 */
	final public static double OFFSET_2000M_LONGITUDE = 1000.0 / MI_PER_LONGITUDE;
	/**
	 * 5000M范围之内的纬度偏移
	 */
	final public static double OFFSET_5000M_LATITUDE = 2500.0 / MI_PER_LATITUDE;
	/**
	 * 5000M范围之内的经度偏移
	 */
	final public static double OFFSET_5000M_LONGITUDE = 2500.0 / MI_PER_LONGITUDE;
	/**
	 * 10000M范围之内的纬度偏移
	 */
	final public static double OFFSET_10000M_LATITUDE = 5000.0 / MI_PER_LATITUDE;
	/**
	 * 10000M范围之内的经度偏移
	 */
	final public static double OFFSET_10000M_LONGITUDE = 5000.0 / MI_PER_LONGITUDE;
	/**
	 * 100000M范围之内的纬度偏移
	 */
	final public static double OFFSET_100000M_LATITUDE = 20000.0 / MI_PER_LATITUDE;
	/**
	 * 100000M范围之内的经度偏移
	 */
	final public static double OFFSET_100000M_LONGITUDE = 20000.0 / MI_PER_LONGITUDE;

	public static void main(String[] args) {
		System.out.println(getDistance(31.980298, 117.107277, 31.888227, 117.524757));
	}

	private static double EARTH_RADIUS = 6378137;// 地球半径

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		// s = Math.round(s * 10000) / 10000;
		return s;
	}
}
