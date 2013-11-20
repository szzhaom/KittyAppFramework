package kitty.kaf.util;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class RSListIterator<E> implements ListIterator<E> {
	/** 数据数组 **/
	private final Object[] snapshot;
	/** 当前光标. */
	private int cursor;

	/**
	 * 构建一个列表迭代器
	 * 
	 * @param elements
	 *            元素数组
	 * @param initialCursor
	 *            初始光标
	 * @param looped
	 *            是否循环构造迭代器<br>
	 *            正常情况下，迭代器的数据和elements中的顺序是一样的。当指定了looped=true时，
	 *            则迭代器中的数据以initialCursor指定的数据作为初始元素，示意如下：<br>
	 *            设initialCursor=5， elements: 0 1 2 3 4 5 6 7 8 9 <br>
	 *            则迭代器中的元素顺序为： 5 6 7 8 9 0 1 2 3 4
	 */
	public RSListIterator(E[] elements, int initialCursor, boolean looped) {
		cursor = initialCursor;
		snapshot = new Object[elements.length];
		if (looped) {
			for (int i = initialCursor; i < elements.length; i++) {
				snapshot[i] = elements[i];
			}
			for (int i = 0; i < initialCursor; i++) {
				snapshot[i] = elements[i];
			}

		} else
			for (int i = 0; i < elements.length; i++) {
				snapshot[i] = elements[i];
			}
	}

	/**
	 * 构建一个列表迭代器
	 * 
	 * @param elements
	 *            元素列表
	 * @param initialCursor
	 *            初始光标
	 * @param looped
	 *            是否循环构造迭代器<br>
	 *            正常情况下，迭代器的数据和elements中的顺序是一样的。当指定了looped=true时，
	 *            则迭代器中的数据以initialCursor指定的数据作为初始元素，示意如下：<br>
	 *            设initialCursor=5， elements: 0 1 2 3 4 5 6 7 8 9 <br>
	 *            则迭代器中的元素顺序为： 5 6 7 8 9 0 1 2 3 4
	 */
	public RSListIterator(List<E> elements, int initialCursor, boolean looped) {
		cursor = initialCursor;
		snapshot = new Object[elements.size()];
		if (looped) {
			for (int i = initialCursor; i < snapshot.length; i++) {
				snapshot[i] = elements.get(i);
			}
			for (int i = 0; i < initialCursor; i++) {
				snapshot[i] = elements.get(i);
			}

		} else
			for (int i = 0; i < snapshot.length; i++) {
				snapshot[i] = elements.get(i);
			}
	}

	public boolean hasNext() {
		return cursor < snapshot.length;
	}

	public boolean hasPrevious() {
		return cursor > 0;
	}

	@SuppressWarnings("unchecked")
	public E next() {
		if (!hasNext())
			throw new NoSuchElementException();
		return (E) snapshot[cursor++];
	}

	@SuppressWarnings("unchecked")
	public E previous() {
		if (!hasPrevious())
			throw new NoSuchElementException();
		return (E) snapshot[--cursor];
	}

	public int nextIndex() {
		return cursor;
	}

	public int previousIndex() {
		return cursor - 1;
	}

	/**
	 * 未实现. 总是抛出 UnsupportedOperationException.
	 * 
	 * @throws UnsupportedOperationException
	 *             总是抛出该异常; 本迭代器不支持<tt>remove</tt> 接口.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 未实现. 总是抛出 UnsupportedOperationException.
	 * 
	 * @throws UnsupportedOperationException
	 *             总是抛出该异常; 本迭代器不支持<tt>set</tt> 接口.
	 */
	public void set(E e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 未实现. 总是抛出 UnsupportedOperationException.
	 * 
	 * @throws UnsupportedOperationException
	 *             总是抛出该异常; 本迭代器不支持<tt>add</tt> 接口.
	 */
	public void add(E e) {
		throw new UnsupportedOperationException();
	}

}
