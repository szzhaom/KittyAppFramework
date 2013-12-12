package kitty.kaf.dao.table;

import java.io.IOException;

import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.Valuable;

public enum ColumnDataType implements Valuable<Integer> {

	BOOLEAN {

		public String toString() {
			return "布尔";
		}

		public Integer getValue() {
			return 0;
		}
	},
	BYTE {

		public String toString() {
			return "字节";
		}

		public Integer getValue() {
			return 1;
		}
	},
	SHORT {

		public String toString() {
			return "短整数";
		}

		public Integer getValue() {
			return 2;
		}
	},
	INT {

		public String toString() {
			return "整数";
		}

		public Integer getValue() {
			return 3;
		}
	},
	LONG {

		public String toString() {
			return "长整数";
		}

		public Integer getValue() {
			return 4;
		}
	},
	FLOAT {

		public String toString() {
			return "浮点数";
		}

		public Integer getValue() {
			return 5;
		}
	},
	DOUBLE {

		public String toString() {
			return "双精度数字";
		}

		public Integer getValue() {
			return 6;
		}
	},
	DATE {

		public String toString() {
			return "日期";
		}

		public Integer getValue() {
			return 7;
		}
	},
	STRING {

		public String toString() {
			return "字节串";
		}

		public Integer getValue() {
			return 8;
		}
	};

	public void setValue(Integer v) {
		throw new UnsupportedOperationException();
	}

	public String getText() {
		return toString();
	}

	public static ColumnDataType valueOf(int value) {
		ColumnDataType[] values = ColumnDataType.values();
		for (ColumnDataType o : values)
			if (o.getValue() == value)
				return o;
		return values[0];
	}

	public static ColumnDataType valueOfObject(Object str) {
		if (str == null)
			return ColumnDataType.values()[0];
		return valueOf(Integer.valueOf(str.toString()));
	}

	public static ColumnDataType readFromStream(DataRead stream) throws IOException {
		return valueOf(stream.readByte());
	}

	public static void writeToStream(ColumnDataType v, DataWrite stream) throws IOException {
		stream.writeByte(v.getValue());
	}
}
