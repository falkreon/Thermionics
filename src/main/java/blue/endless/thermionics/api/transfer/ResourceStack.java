package blue.endless.thermionics.api.transfer;

import java.util.Objects;
import java.util.Optional;

public record ResourceStack<T>(Resource<T> resource, long count) {
	public ResourceStack(Resource<T> resource, long count) {
		if (count <= 0L) throw new IllegalArgumentException("Count must be at least 1.");
		this.resource = resource;
		this.count = count;
	}
	
	public boolean isOf(Resource<T> resource) {
		return Objects.equals(this.resource, resource);
	}
	
	public boolean isOf(T resource) {
		return Objects.equals(this.resource.object(), resource);
	}
	
	public OptionalStack<T> decrement() {
		if (count <= 1L) return OptionalStack.empty();
		
		return OptionalStack.of(new ResourceStack<>(resource, count - 1L));
	}
	
	public OptionalStack<T> copyWithCount(long newCount) {
		if (newCount <= 0L) return OptionalStack.empty();
		
		return OptionalStack.of(new ResourceStack<>(resource, newCount));
	}
	
	/**
	 * The result of splitting a ResourceStack
	 * @param <T> the kind of Resource (e.g. Item, Fluid)
	 */
	public static record SplitResult<T>(
			/**
			 * The stack that was successfully split out from the source ResourceStack, or empty if
			 * nothing could be removed
			 */
			OptionalStack<T> split,
			/**
			 * The state of the original resource stack after the split
			 */
			OptionalStack<T> remaining
			) {}
	
	public SplitResult<T> split(long count) {
		// Reject all?
		if (count <= 0) return new SplitResult<>(OptionalStack.empty(), OptionalStack.of(this));
				
		// Transfer all?
		if (this.count <= count) return new SplitResult<>(OptionalStack.of(this), OptionalStack.empty());
				
		// stack size is > count, so transfer Some
		long accepted = count;
		long remainder = this.count - accepted;
				
		return new SplitResult<>(copyWithCount(accepted), copyWithCount(remainder));
	}
	
	/**
	 * The result of attempting to merge two ResourceStacks together
	 * @param <T> the kind of Resource (e.g. Item, Fluid)
	 */
	public static record MergeResult<T>(
			/**
			 * The state of this stack after the other stack attempts to merge into it. In other
			 * words, the resulting merged stack.
			 */
			OptionalStack<T> merged,
			/**
			 * The state of the other stack after attempting to put its contents all into this
			 * stack. In other words, the rejected resources.
			 */
			OptionalStack<T> rejected
			) {}
	
	public MergeResult<T> merge(ResourceStack<T> other, long sizeLimit) {
		// incompatible dest, reject all
		if (!Objects.equals(this.resource(), other.resource())) return new MergeResult<>(OptionalStack.of(this), OptionalStack.of(other));
		
		long mergedSize = this.count() + other.count();
		long resultSize = Math.min(mergedSize, sizeLimit);
		long remainderSize = mergedSize - resultSize;
		return (remainderSize <= 0) ?
			new MergeResult<>(copyWithCount(resultSize), OptionalStack.empty()) :
			new MergeResult<>(copyWithCount(mergedSize), copyWithCount(remainderSize));
	}
	
	public MergeResult<T> merge(OptionalStack<T> other, long sizeLimit) {
		if (other.isEmpty()) {
			return new MergeResult<>(OptionalStack.of(this), OptionalStack.empty());
		} else {
			return merge(other.get(), sizeLimit);
		}
	}
	
	public static <T> MergeResult<T> merge(OptionalStack<T> dest, OptionalStack<T> src, long sizeLimit) {
		if (dest.isEmpty()) return new MergeResult<>(dest, src);
		return dest.get().merge(src, sizeLimit);
	}
	
	public static <T> SplitResult<T> split(OptionalStack<T> src, long count) {
		if (src.isEmpty()) return new SplitResult<>(OptionalStack.empty(), OptionalStack.empty());
		
		return src.get().split(count);
	}
	
	public static <T> long count(Optional<ResourceStack<T>> stack) {
		return stack.map(ResourceStack::count).orElse(0L);
	}
}
