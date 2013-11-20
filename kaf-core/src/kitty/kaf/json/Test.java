package kitty.kaf.json;

import java.util.concurrent.ConcurrentHashMap;

class TestA implements Cloneable {
	int x, y;
	ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<Object, Object>();

	public Object copy() throws CloneNotSupportedException {
		return super.clone();
	}
}

public class Test {
	public static void main(String args[]) throws CloneNotSupportedException {
		TestA a = new TestA();
		a.x = 10;
		a.y = 1;
		a.attributes.put("asdff", 1);
		a.attributes.put("asdfsafd", "asdf");
		TestA b = (TestA) a.copy();
		System.out.print(b.x);
	}
}
