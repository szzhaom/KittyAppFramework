package kitty.kaf.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionHelper {

	public static String collectionToString(Object value) {
		if (value == null)
			return "";
		StringBuffer sb = new StringBuffer();
		if (value instanceof Object[]) {
			for (Object o : (Object[]) value) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(o);
			}
		} else if (value instanceof Collection<?>) {
			for (Object o : (Collection<?>) value) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(o);
			}
		} else
			sb.append(value);
		return sb.toString();
	}

	public static List<Object> buildList(Object... params) {
		List<Object> r = new ArrayList<Object>();
		for (Object o : params)
			r.add(o);
		return r;
	}

	public static List<Object> arrayToList(Object[] params) {
		List<Object> r = new ArrayList<Object>();
		for (Object o : params)
			r.add(o);
		return r;
	}

	public static Object[] listToArray(List<Object> params) {
		Object[] r = new Object[params.size()];
		int i = 0;
		for (Object o : params)
			r[i++] = o;
		return r;
	}

	public static <E> boolean contains(E[] a, E[] v) {
		for (E o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static <E> boolean contains(E[] a, E v) {
		for (E o : a) {
			if (o.equals(v))
				return true;
		}
		return false;
	}

	public static boolean contains(int[] a, int v) {
		for (int o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(int[] a, int[] v) {
		for (int o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(byte[] a, byte v) {
		for (byte o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(byte[] a, byte[] v) {
		for (byte o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(short[] a, short v) {
		for (short o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(short[] a, short[] v) {
		for (short o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(long[] a, long v) {
		for (long o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(long[] a, long[] v) {
		for (long o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(float[] a, float v) {
		for (float o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(float[] a, float[] v) {
		for (float o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

	public static boolean contains(double[] a, double v) {
		for (double o : a) {
			if (o == v)
				return true;
		}
		return false;
	}

	public static boolean contains(double[] a, double[] v) {
		for (double o : v) {
			if (!contains(a, o))
				return false;
		}
		return true;
	}

}
