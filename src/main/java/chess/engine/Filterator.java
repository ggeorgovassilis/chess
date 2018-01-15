package chess.engine;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Iterator that takes a source iterator as input and transforms its elements into a different type.
 * Nulls returned by the source iterator will be ignored.
 * @param <S> Type of the source iterator
 * @param <T> Returned  type
 */
public class Filterator<S, T> implements Iterator<T> {

	final Iterator<S> source;
	final Function<S, T> mapper;
	T next = null;

	/**
	 * 
	 * @param source Source iterator, returns <S>-typed elements
	 * @param mapper Mapper that transforms <S> to <T>
	 */
	public Filterator(Iterator<S> source, Function<S, T> mapper) {
		this.source = source;
		this.mapper = mapper;
		this.next = getNextTargetItem();
	}

	protected S getNextSourceItem() {
		S nextSource = null;
		while (nextSource == null && source.hasNext())
			nextSource = source.next();
		return nextSource;
	}

	protected T getNextTargetItem() {
		while (source.hasNext()) {
			S nextSourceItem = getNextSourceItem();
			if (nextSourceItem == null)
				continue;
			T nextTarget = mapper.apply(nextSourceItem);
			if (nextTarget!=null)
				return nextTarget;
		}
		return null;
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public T next() {
		T value = next;
		next = getNextTargetItem();
		if (value == null)
			throw new RuntimeException("No next item");
		return value;
	}

}
