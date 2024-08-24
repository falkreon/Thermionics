package blue.endless.thermionics.api.transfer.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import blue.endless.thermionics.api.MassResource;
import blue.endless.thermionics.api.transfer.Transfer;
import blue.endless.thermionics.api.transfer.VariantStack;

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
	public VariantStack<MassResource> insert(VariantStack<MassResource> stack, boolean simulate) {
		// These early checks are also overed by merge, but it's faster to early-reject here.
		if (stack.variant().hasComponents() || !Objects.equals(stack.variant().getObject(), resource)) return Transfer.MASS_RESOURCE.blank();
		
		Transfer.Result<MassResource> transfer = Transfer.MASS_RESOURCE.merge(getStack(), stack, limit);
		
		if (!simulate && transfer.result().count() != count) {
			count = transfer.result().count();
			fireChanges();
		}
		
		return transfer.rejected();
	}

	@Override
	protected VariantStack<MassResource> extract(long amount, boolean simulate) {
		Transfer.Result<MassResource> transfer = Transfer.MASS_RESOURCE.split(getStack(), amount);
		
		if (!simulate && !transfer.result().isEmpty()) {
			count = transfer.rejected().count();
			fireChanges();
		}
		
		return transfer.result();
	}

	@Override
	protected VariantStack<MassResource> getStack() {
		return new VariantStack<>(MassResource.Variant.of(resource), count);
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
	
	@Override
	protected Transfer<MassResource> getTransfer() {
		return Transfer.MASS_RESOURCE;
	}
	
	private void fireChanges() {
		for(Runnable r : changeListeners) r.run();
	}
	
}
