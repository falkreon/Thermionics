package blue.endless.thermionics.api.transfer.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import blue.endless.thermionics.api.MassResource;
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
	public Optional<ResourceStack<MassResource>> insert(ResourceStack<MassResource> stack, boolean simulate) {
		// These early checks are also overed by merge, but it's faster to early-reject here.
		if (stack.resource().hasComponents() || !Objects.equals(stack.resource().object(), resource)) return Optional.empty();
		
		ResourceStack.MergeResult<MassResource> transfer = ResourceStack.merge(getStack(), Optional.of(stack), limit);
		
		if (!simulate && ResourceStack.count(transfer.merged()) != count) {
			count = ResourceStack.count(transfer.merged());
			fireChanges();
		}
		
		return transfer.rejected();
	}

	@Override
	protected Optional<ResourceStack<MassResource>> extract(long amount, boolean simulate) {
		if (this.count == 0L) return Optional.empty();
		
		Optional<ResourceStack<MassResource>> existing = getStack();
		if (existing.isEmpty()) return Optional.empty(); // Shouldn't happen because of check above
		
		ResourceStack.SplitResult<MassResource> transfer = existing.get().split(amount);
		
		if (!simulate && !transfer.split().isEmpty()) {
			count = ResourceStack.count(transfer.remaining());
			fireChanges();
		}
		
		return transfer.split();
	}

	@Override
	protected Optional<ResourceStack<MassResource>> getStack() {
		if (count <= 0L) return Optional.empty();
		return Optional.of(new ResourceStack<>(new Resource<>(resource), count));
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
