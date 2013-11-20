package kitty.kaf.io;

/**
 * 使数据对象同时具备表示一个ID关键字和复选的能力。
 * <p>
 * 通过实现<code>IdCheckable</code>接口，使数据对象具备表示一个ID关键字和复选的能力
 * </p>
 * 
 * @author 赵明
 * @param E
 *            表示关键值(即getID())的类型
 * @version 1.0
 */
public interface IdCheckable<E> extends Idable<E>, Checkable {

}
