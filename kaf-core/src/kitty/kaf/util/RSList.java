package kitty.kaf.util;

import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RSList<E> extends CopyOnWriteArrayList<E> {
	private static final long serialVersionUID = -2503882973144737974L;
	private AtomicInteger currentIndex = new AtomicInteger(0);

	/**
	 * 从0开始循环获取下一个元素，到最大索引时，再从0开始
	 * 
	 * @return 下一个元素
	 */
	public E next() {
		if (size() == 0)
			return null;
		int index = currentIndex.getAndAdd(1);
		if (index >= size())
			index = 0;
		return get(index);
	}

	/**
	 * 创建一个列表迭代器，迭代器的初始光标(即next()第一个返回的元素)为beginIndex指定的元素
	 * 
	 * @param initialCursor
	 *            初始光标
	 * @param looped
	 *            是否循环构造迭代器<br>
	 *            正常情况下，迭代器的数据和elements中的顺序是一样的。当指定了looped=true时，
	 *            则迭代器中的数据以initialCursor指定的数据作为初始元素，示意如下：<br>
	 *            设initialCursor=5， elements: 0 1 2 3 4 5 6 7 8 9 <br>
	 *            则迭代器中的元素顺序为： 5 6 7 8 9 0 1 2 3 4
	 * @return 构建的迭代器
	 */
	public ListIterator<E> listIterator(int beginIndex, boolean looped) {
		return new RSListIterator<E>(this, beginIndex, looped);
	}
}
