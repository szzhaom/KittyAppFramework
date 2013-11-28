package kitty.kaf.dao.table;

import java.io.IOException;

import kitty.kaf.io.Cachable;
import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.Idable;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;

/**
 * 包含id的表对象
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 * @param <E>
 *            id的类名
 */
abstract public class IdTableObject<E> extends TableObject implements Cachable<E> {
	private static final long serialVersionUID = 1L;
	E id;
	private boolean isNull;

	/**
	 * 比较两个id的大小。compareTo函数中会调用该接口比较两个对象的大小
	 * 
	 * @param id1
	 *            第1个id
	 * @param id2
	 *            第2个id
	 * @return 0 - id1=id2<br>
	 *         负数 - id1<id2<br>
	 *         正数 - id1>id2
	 */
	abstract protected int compareId(E id1, E id2);

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Idable<E> other) {
		if (this == other)
			return 0;
		if (other == null)
			throw new NullPointerException();
		if (getClass() != other.getClass())
			throw new ClassCastException();
		IdTableObject<?> o = (IdTableObject<?>) other;
		if (id == null) {
			if (o.id != null)
				return 1;
		} else
			return compareId(id, (E) o.id);
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyData(TableObject other) {
		if (other == null)
			throw new NullPointerException();
		if (!(other instanceof IdTableObject<?>))
			throw new ClassCastException();
		super.copyData(other);
		IdTableObject<?> o = (IdTableObject<?>) other;
		setId((E) o.getId());
	}

	@Override
	final public void writeToStream(DataWrite stream) throws IOException {
		stream.writeBoolean(isNull);
		if (!isNull)
			doWriteToStream(stream);
	}

	protected void doWriteToStream(DataWrite stream) throws IOException {
		super.writeToStream(stream);
	}

	@Override
	final public void readFromStream(DataRead stream) throws IOException {
		setNull(stream.readBoolean());
		if (!isNull)
			doReadFromStream(stream);
	}

	protected void doReadFromStream(DataRead stream) throws IOException {
		super.readFromStream(stream);
	}

	@Override
	public E getId() {
		return id;
	}

	@Override
	public void setId(E id) {
		this.id = id;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	@Override
	public void toJson(JSONObject json) throws JSONException {
		super.toJson(json);
		json.put("id", getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdTableObject<?> other = (IdTableObject<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
