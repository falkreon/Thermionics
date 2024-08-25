package blue.endless.thermionics.api.transfer;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import blue.endless.thermionics.api.transfer.ResourceStack.MergeResult;
import blue.endless.thermionics.api.transfer.ResourceStack.SplitResult;
import net.minecraft.component.ComponentChanges;

public sealed interface OptionalStack<T> permits OptionalStack.Of, OptionalStack.Empty {
	
	static final OptionalStack<Object> EMPTY = new Empty<>();
	
	@SuppressWarnings("unchecked")
	public static <T> OptionalStack<T> empty() {
		return (OptionalStack<T>) EMPTY;
	}
	
	public static <T> OptionalStack<T> of(ResourceStack<T> stack) {
		return new Of<>(stack);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> OptionalStack<T> ofNullable(ResourceStack<T> stack) {
		if (stack == null) return (OptionalStack<T>) EMPTY;
		return new Of<>(stack);
	}
	
	
	
	public boolean isPresent();
	
	public boolean isEmpty();
	
	public ResourceStack<T> get();
	
	public ResourceStack<T> orElse(ResourceStack<T> other);
	
	public ResourceStack<T> orElseGet(Supplier<ResourceStack<T>> supplier);
	
	public Optional<Resource<T>> resource();
	
	public boolean isOf(Resource<T> resource);
	
	public boolean isOf(T resource);
	
	public long count();
	
	public <U> Optional<U> map(Function<ResourceStack<T>, U> mapper);
	
	public Stream<ResourceStack<T>> stream();
	
	public boolean hasComponents();
	
	public ComponentChanges components();
	
	public default MergeResult<T> merge(ResourceStack<T> other, long sizeLimit) {
		return ResourceStack.merge(this, OptionalStack.of(other), sizeLimit);
	}
	
	public default MergeResult<T> merge(OptionalStack<T> other, long sizeLimit) {
		return ResourceStack.merge(this, other, sizeLimit);
	}
	
	public default SplitResult<T> split(long count) {
		return ResourceStack.split(this, count);
	}
	
	
	public static final class Of<T> implements OptionalStack<T> {
		private ResourceStack<T> stack;
		
		private Of(ResourceStack<T> stack) {
			Objects.requireNonNull(stack);
			this.stack =  stack;
		}
		
		@Override
		public boolean isPresent() {
			return true;
		}
		
		@Override
		public boolean isEmpty() {
			return false;
		}
		
		@Override
		public ResourceStack<T> get() {
			return stack;
		}
		
		@Override
		public ResourceStack<T> orElse(ResourceStack<T> other) {
			return stack;
		}
		
		@Override
		public ResourceStack<T> orElseGet(Supplier<ResourceStack<T>> supplier) {
			return stack;
		}
		
		@Override
		public Optional<Resource<T>> resource() {
			return Optional.of(stack.resource());
		}
		
		@Override
		public boolean isOf(Resource<T> resource) {
			return Objects.equals(stack.resource(), resource);
		}
		
		@Override
		public boolean isOf(T resource) {
			return Objects.equals(stack.resource().object(), resource);
		}
		
		@Override
		public long count() {
			return stack.count();
		}

		@Override
		public <U> Optional<U> map(Function<ResourceStack<T>, U> mapper) {
			return Optional.of(mapper.apply(stack));
		}
		
		@Override
		public Stream<ResourceStack<T>> stream() {
			return Stream.of(stack);
		}
		
		@Override
		public boolean hasComponents() {
			return stack.resource().hasComponents();
		}
		
		@Override
		public ComponentChanges components() {
			return stack.resource().components();
		}
		
	}
	
	public static final class Empty<T> implements OptionalStack<T> {
		private Empty() {}
		
		@Override
		public boolean isPresent() {
			return false;
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public ResourceStack<T> get() {
			throw new NoSuchElementException();
		}
		
		@Override
		public ResourceStack<T> orElse(ResourceStack<T> other) {
			return other;
		}
		
		@Override
		public ResourceStack<T> orElseGet(Supplier<ResourceStack<T>> supplier) {
			return supplier.get();
		}
		
		@Override
		public Optional<Resource<T>> resource() {
			return Optional.empty();
		}
		
		@Override
		public boolean isOf(Resource<T> resource) {
			return false;
		}
		
		@Override
		public boolean isOf(T resource) {
			return false;
		}
		
		@Override
		public long count() {
			return 0L;
		}
		
		@Override
		public <U> Optional<U> map(Function<ResourceStack<T>, U> mapper) {
			return Optional.empty();
		}
		
		@Override
		public Stream<ResourceStack<T>> stream() {
			return Stream.empty();
		}
		
		@Override
		public boolean hasComponents() {
			return false;
		}
		
		@Override
		public ComponentChanges components() {
			return ComponentChanges.EMPTY;
		}
		
	}
}
