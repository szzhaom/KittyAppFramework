package kitty.kaf.listeners;

/**
 * 创建对象的事件侦听器
 * 
 * @author 赵明
 * @version 1.0
 * @param <V>
 */
public interface ObjectCreateListener<V> {
	public V createObject();
}
