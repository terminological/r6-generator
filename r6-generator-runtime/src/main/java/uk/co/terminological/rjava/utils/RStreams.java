package uk.co.terminological.rjava.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import uk.co.terminological.rjava.types.RPrimitive;
import uk.co.terminological.rjava.types.RVector;

/**
 * The Class Java8Streams can be statically imported and allows any type to be flatMapped.
 * e.g. streamOfMaybeListsContainingNullValues.flatMap(l -&gt; maybe(l.getOptionalValue()));
 *
 * @author vp22681
 * @version $Id: $Id
 */
public class RStreams {

	/**
	 * Create a stream from a potentially null singleton or array
	 *
	 * @param t a set of values
	 * @param <T> any type
	 * @return a stream
	 */
	@SafeVarargs
	public static <T> Stream<T> maybe(T... t) {
		return t == null ? Stream.empty() : Stream.of(t);
	}

	/**
	 * Create a stream from a potentially null collection
	 *
	 * @param list a collection of values
	 * @param <T> any type
	 * @return a stream
	 */
	public static <T> Stream<T> maybe(Collection<T> list) {
		return list == null ? Stream.empty() : list.stream();
	}

	/**
	 * Create a stream from a potentially null stream
	 *
	 * @param stream a stream
	 * @param <T> any type
	 * @return a stream
	 */
	public static <T> Stream<T> maybe(Stream<T> stream) {
		return stream == null ? Stream.empty() : stream;
	}

	/**
	 * Create a stream from a potentially null optional
	 *
	 * @param t an optional value
	 * @param <T> any type
	 * @return a stream
	 */
	public static <T> Stream<T> maybe(Optional<T> t) {
		return t.map(o -> Stream.of(o)).orElse(Stream.empty());
	}

	/**
	 * Create a stream from a potentially null iterable
	 *
	 * @param iter an iterable of values
	 * @param <T> any type
	 * @return a stream
	 */
	public static <T> Stream<T> maybe(Iterable<T> iter) {
		return iter == null ? Stream.empty() : StreamSupport.stream(iter.spliterator(), false );
	}

	/**
	 * Create a stream from a potentially null iterator
	 *
	 * @param iter an iterator
	 * @param <T> any type
	 * @return a stream
	 */
	public static <T> Stream<T> maybe(Iterator<T> iter) {
		return iter == null ? Stream.empty() : StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(
						iter,
						Spliterator.ORDERED)
				, false);
	}

	/**
	 * Create a stream from a potentially null or NA valued RPrimitive
	 *
	 * @param t an RPrimitive
	 * @param <T> any type
	 * @return a stream of the underlying java object omitting NA values
	 */
	public static <T> Stream<T> maybe(RPrimitive t) {
		return t == null ? Stream.empty() : maybe(t.opt());
	}

	/**
	 * Create a stream from a potentially null or NA valued RVector
	 *
	 * @param t an RVector of type U
	 * @param <T> any type
	 * @param <U> an RPrimitive
	 * @return a stream of the underlying java object omitting NA values
	 */
	public static <U extends RPrimitive, T> Stream<T> maybe(RVector<U> t) {
		return t == null ? Stream.empty() : t.stream().flatMap(u -> maybe(u.opt()));
	}

	/**
	 * <p>deferError.</p>
	 *
	 * @param fn a {@link uk.co.terminological.rjava.utils.RStreams.ExceptionalFunction} object
	 * @param <T> a T class
	 * @param <R> a R class
	 * @return a {@link java.util.function.Function} object
	 */
	public static <T,R> Function<T, Exceptional<R>> deferError(ExceptionalFunction<T, R> fn) {
		return new Function<T, Exceptional<R>>() {
			@Override
			public Exceptional<R> apply(T t) {
				try {
					return Exceptional.success(fn.apply(t));
				} catch (Exception e) {
					return Exceptional.failed(e);
				}
			}
		};
	}

	@FunctionalInterface
	public static interface ExceptionalFunction<T, R> {
		public R apply(T in) throws Exception; 
	}

	public static class FilteredException extends Exception {

		private FilteredException() {
			super();
		}

		private FilteredException(Exception e) {
			super(e);
		}

		public static FilteredException wrap(Exception e) {
			if (e == null) return new FilteredException();
			if (e instanceof FilteredException) return (FilteredException) e;
			return new FilteredException(e);
		}


	}

	public static class Exceptional<T>  {

		/**
		 * If non-null, the value; if null, indicates no value is present
		 */
		private final T value;
		private final Exception exception;

		/**
		 * Constructs an instance with the described value.
		 *
		 * @param value the value to describe; it's the caller's responsibility to
		 *        ensure the value is non-{@code null} unless creating the singleton
		 *        instance returned by {@code empty()}.
		 */
		private Exceptional(T value) {
			this.value = value;
			this.exception = null;
		}

		private Exceptional(Exception exception) {
			this.value = null;
			this.exception = exception;
		}

		public static <T,E extends Exception> Exceptional<T> success(T value) {
			return new Exceptional<>(value);
		}

		public static <T,E extends Exception> Exceptional<T> failed(Exception exception) {
			return new Exceptional<>(exception);
		}

		public Exceptional<T> filtered() {
			return new Exceptional<>(FilteredException.wrap(exception));
		}

		/**
		 * If a value is present, returns the value, otherwise throws
		 * {@code NoSuchElementException}.
		 *
		 * @return the non-{@code null} value described by this {@code Optional}
		 * @throws Exception if no value is present
		 */
		public T get() throws Exception {
			if (value == null) {
				throw exception;
			}
			return value;
		}

		/**
		 * If a value is present, returns {@code true}, otherwise {@code false}.
		 *
		 * @return {@code true} if a value is present, otherwise {@code false}
		 */
		public boolean succeeded() {
			return value != null;
		}

		/**
		 * If a exception was throwb, returns {@code true}, otherwise {@code false}.
		 *
		 * @return {@code true} if a value is present, otherwise {@code false}
		 */
		public boolean failed() {
			return exception != null;
		}


		/**
		 * If a value is present, performs the given action with the value,
		 * otherwise does nothing.
		 *
		 * @param action the action to be performed, if a value is present
		 * @throws NullPointerException if value is present and the given action is
		 *         {@code null}
		 */
		public void onSuccess(Consumer<? super T> action) {
			if (value != null) {
				action.accept(value);
			}
		}

		/**
		 * If a value is present, performs the given action with the value,
		 * otherwise performs the given empty-based action.
		 *
		 * @param action the action to be performed, if a value is present
		 * @param errorHandler the action to be performed, if an error was thrown
		 * @throws NullPointerException if a value is present and the given action
		 *         is {@code null}, or no value is present and the given empty-based
		 *         action is {@code null}.
		 * @since 9
		 */
		public void onSucessOrFailure(Consumer<? super T> action, Consumer<Exception> errorHandler) {
			Objects.requireNonNull(action);
			Objects.requireNonNull(errorHandler);
			if (value != null) {
				action.accept(value);
			} else {
				errorHandler.accept(exception);
			}
		}

		/**
		 * If the exceptional has a value, and the value matches the given predicate,
		 * returns an {@code Exceptional} describing the value, otherwise a
		 * {@code Exceptional} failing due to a {@code FilteredException}.
		 *
		 * @param predicate the predicate to apply to a value, if present
		 * @return an {@code Exceptional} describing the value of this
		 *         {@code Exceptional}, if a value is present and the value matches the
		 *         given predicate, otherwise a failing {@code Exceptional}
		 * @throws NullPointerException if the predicate is {@code null}
		 */
		public Exceptional<T> filter(Predicate<? super T> predicate) {
			Objects.requireNonNull(predicate);
			if (!succeeded()) {
				return this;
			} else {
				return predicate.test(value) ? this : filtered();
			}
		}

		/**
		 * If a value is present, returns an {@code Exceptional} containing the 
		 * result of applying the given mapping function to
		 * the value, otherwise returns a failing {@code Exceptional}.
		 *
		 * <p>If the mapping function returns a {@code null} result then this method
		 * returns an empty {@code Exceptional}.
		 *
		 * @param mapper the mapping function to apply to a value, if present
		 * @param <U> The type of the value returned from the mapping function
		 * @return an {@code Exceptional} describing the result of applying a mapping
		 *         function to the value of this {@code Exceptional}, if a value is
		 *         present, otherwise an empty {@code Exceptional}
		 * @throws NullPointerException if the mapping function is {@code null}
		 */
		public <U> Exceptional<U> map(ExceptionalFunction<? super T, ? extends U> mapper) {
			Objects.requireNonNull(mapper);
			if (!succeeded()) {
				return failed(exception);
			} else {
				try {
					return success(mapper.apply(value));
				} catch (Exception e) {
					return failed(e);
				}
			}
		}

		/**
		 * If a value is present, returns the result of applying the given
		 * {@code Exceptional}-bearing mapping function to the value, otherwise returns
		 * an empty {@code Exceptional}.
		 *
		 * <p>This method is similar to {@link #map(ExceptionalFunction)}, but the mapping
		 * function is one whose result is already an {@code Exceptional}, and if
		 * invoked, {@code flatMap} does not wrap it within an additional
		 * {@code Exceptional}.
		 *
		 * @param <U> The type of value of the {@code Exceptional} returned by the
		 *            mapping function
		 * @param mapper the mapping function to apply to a value, if present
		 * @return the result of applying an {@code Exceptional}-bearing mapping
		 *         function to the value of this {@code Exceptional}, if a value is
		 *         present, otherwise an empty {@code Exceptional}
		 * @throws NullPointerException if the mapping function is {@code null} or
		 *         returns a {@code null} result
		 */
		public <U> Exceptional<U> flatMap(Function<? super T, ? extends Exceptional<? extends U>> mapper) {
			Objects.requireNonNull(mapper);
			if (!succeeded()) {
				return failed(exception);
			} else {
				@SuppressWarnings("unchecked")
				Exceptional<U> r = (Exceptional<U>) mapper.apply(value);
				return Objects.requireNonNull(r);
			}
		}

		/**
		 * If a value is present, returns a sequential {@link Stream} containing
		 * only that value, otherwise returns an empty {@code Stream}.
		 *
		 * @return the Exceptional value as a {@code Stream}
		 * @since 9
		 */
		public Stream<T> stream() {
			if (!succeeded()) {
				return Stream.empty();
			} else {
				return Stream.of(value);
			}
		}

		/**
		 * If a value is present, returns the value, otherwise returns
		 * {@code other}.
		 *
		 * @param other the value to be returned, if no value is present.
		 *        May be {@code null}.
		 * @return the value, if present, otherwise {@code other}
		 */
		public T orElse(T other) {
			return succeeded() ? value : other;
		}

		/**
		 * If a value is present, returns the value, otherwise returns the result
		 * produced by the supplying function.
		 *
		 * @param supplier the supplying function that produces a value to be returned
		 * @return the value, if present, otherwise the result produced by the
		 *         supplying function
		 * @throws NullPointerException if no value is present and the supplying
		 *         function is {@code null}
		 */
		public T orElseGet(Supplier<? extends T> supplier) {
			return succeeded() ? value : supplier.get();
		}

		/**
		 * If a value is present, returns the value, otherwise throws
		 * {@code NoSuchElementException}.
		 *
		 * @return the non-{@code null} value described by this {@code Exceptional}
		 * @throws Exception 
		 * @since 10
		 */
		public T orElseThrow() throws Exception {
			if (!succeeded()) {
				throw exception;
			}
			return value;
		}

		/**
		 * Indicates whether some other object is "equal to" this {@code Exceptional}.
		 * The other object is considered equal if:
		 * <ul>
		 * <li>it is also an {@code Exceptional} and;
		 * <li>both instances have no value present or;
		 * <li>the present values are "equal to" each other via {@code equals()}.
		 * </ul>
		 *
		 * @param obj an object to be tested for equality
		 * @return {@code true} if the other object is "equal to" this object
		 *         otherwise {@code false}
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			return obj instanceof Exceptional
					&& Objects.equals(value, ((Exceptional<?>) obj).value);
		}

		/**
		 * Returns the hash code of the value, if present, otherwise {@code 0}
		 * (zero) if no value is present.
		 *
		 * @return hash code value of the present value or {@code 0} if no value is
		 *         present
		 */
		@Override
		public int hashCode() {
			return Objects.hashCode(value);
		}

		/**
		 * Returns a non-empty string representation of this {@code Exceptional}
		 * suitable for debugging.  The exact presentation format is unspecified and
		 * may vary between implementations and versions.
		 *
		 * @return the string representation of this instance
		 */
		@Override
		public String toString() {
			return value != null
					? String.format("Success[%s]", value)
							: String.format("Failure[%s]", exception.getMessage());
		}
	}
}
