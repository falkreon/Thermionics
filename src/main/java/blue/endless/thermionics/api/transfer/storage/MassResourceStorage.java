package blue.endless.thermionics.api.transfer.storage;

import java.util.ArrayList;
import java.util.List;

import blue.endless.thermionics.api.MassResource;
import blue.endless.thermionics.api.transfer.OptionalStack;
import blue.endless.thermionics.api.transfer.Resource;
import blue.endless.thermionics.api.transfer.ResourceStack;

/**
 * Typical storage case for batteries and other quantitative tanks. Has one slot of storage, which
 * is locked to a particular MassResource.
 */
public class MassResourceStorage extends OneSlotStorage<MassResource> {
	
	private final MassResource resource;
	private final long limit;
	private long count;
	private List<Runnable> changeListeners = new ArrayList<>();
	
	public MassResourceStorage(MassResource resource, long limit) {
		this.resource = resource;
		this.limit = limit;
	}

	@Override
	public OptionalStack<MassResource> insert(ResourceStack<MassResource> stack, boolean simulate) {
		// These early checks are also covered by merge, but it's faster to early-reject here.
		if (!stack.isOf(resource) || stack.resource().hasComponents()) return OptionalStack.of(stack);
		
		ResourceStack.MergeResult<MassResource> transfer = getStack().merge(stack, limit);
		
		if (!simulate && transfer.merged().count() != count) {
			count = transfer.merged().count();
			fireChanges();
		}
		
		return transfer.rejected();
	}

	@Override
	protected OptionalStack<MassResource> extract(long amount, boolean simulate) {
		if (this.count == 0L) return OptionalStack.empty();
		
		OptionalStack<MassResource> existing = getStack();
		if (existing.isEmpty()) return OptionalStack.empty(); // Shouldn't happen because of check above
		
		ResourceStack.SplitResult<MassResource> transfer = existing.get().split(amount);
		
		if (!simulate && !transfer.split().isEmpty()) {
			count = transfer.remaining().count();
			fireChanges();
		}
		
		return transfer.split();
	}

	@Override
	protected OptionalStack<MassResource> getStack() {
		if (count <= 0L) return OptionalStack.empty();
		return OptionalStack.of(new ResourceStack<>(new Resource<>(resource), count));
	}
	
	public long getCount() {
		return count;
	}
	
	public long getLimit() {
		return limit;
	}
	
	public void registerChangeListener(Runnable listener) {
		changeListeners.add(listener);
	}
	
	private void fireChanges() {
		for(Runnable r : changeListeners) r.run();
	}
	
}
