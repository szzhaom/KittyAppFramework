package kitty.kaf.io;

/**
 * 具备缓存能力的接口
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * @param <E>
 */
public interface Cachable<E> extends Idable<E>, ReadWritable, Nullable {
}
