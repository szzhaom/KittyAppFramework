package kitty.testapp.inf.enumdef;

import java.io.IOException;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.Valuable;

/**
 * 
 * 性别
 * 
 */
public enum Gender implements Valuable<Integer> {

    MALE {

        public String toString() {
            return "男";
        }

        public Integer getValue() {
            return 0;
        }
    }
    , FEMALE {

        public String toString() {
            return "女";
        }

        public Integer getValue() {
            return 1;
        }
    }
    ;

    public void setValue(Integer v) {
        throw new UnsupportedOperationException();
    }

    public String getText() {
        return toString();
    }

    public static Gender valueOf(int value) {
        Gender[] values = Gender.values();
        for (Gender o : values) if (o.getValue() == value) return o;
        return values[0];
    }

    public static Gender valueOfObject(Object str) {
        if (str == null) return Gender.values()[0];
        return valueOf(Integer.valueOf(str.toString()));
    }

    public static Gender readFromStream(DataRead stream) throws IOException {
        return valueOf(stream.readByte());
    }

    public static void writeToStream(Gender v, DataWrite stream) throws IOException {
        stream.writeByte(v.getValue());
    }
}
