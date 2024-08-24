package blue.endless.thermionics.api.transfer.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.include.com.google.common.base.Objects;

import blue.endless.thermionics.api.transfer.Resource;
import blue.endless.thermionics.api.transfer.ResourceStack;
import blue.endless.thermionics.api.transfer.capability.StorageCapability;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.collection.DefaultedList;

public class TransferStorage<T> implements StorageCapability<T> {
	private final long maxStackSize;
	private DefaultedList<Optional<ResourceStack<T>>> contents;
	private List<Runnable> changeListeners = new ArrayList<>();
	
	public TransferStorage(int slots, long maxStackSize) {
		contents = DefaultedList.ofSize(slots, Optional.empty());
		this.maxStackSize = maxStackSize;
	}
	
	public static TransferStorage<Item> items(int slots) {
		return new TransferStorage<>(slots, 64);
	}
	
	public static TransferStorage<Item> singleItemStack() {
		return new TransferStorage<>(1, 64);
	}
	
	public static TransferStorage<Fluid> fluids(int tanks, long tankCapacity) {
		return new TransferStorage<>(tanks, tankCapacity);
	}
	
	public static TransferStorage<Fluid> fluidTank(long tankCapacity) {
		return new TransferStorage<>(1, tankCapacity);
	}
	
	public void addChangeListener(Runnable r) {
		changeListeners.add(r);
	}
	
	protected void fireChanged() {
		for(Runnable r : changeListeners) {
			r.run();
		}
	}
	
	/*
	 * IMPLEMENTS InspectStorageCapability
	 */
	
	@Override
	public Optional<ResourceStack<T>> getSlot(int slotNumber) {
		if (slotNumber < 0 || slotNumber >= contents.size()) return Optional.empty();
		
		return contents.get(slotNumber);
	}

	@Override
	public int getSlotCount() {
		return contents.size();
	}
	
	/*
	 * IMPLEMENTS InsertCapability
	 */
	
	@Override
	public Optional<ResourceStack<T>> insert(int slot, ResourceStack<T> stack, boolean simulate) {
		if (slot < 0 || slot >= contents.size()) return Optional.of(stack);
		Optional<ResourceStack<T>> existing = getSlot(slot);
		
		ResourceStack.MergeResult<T> transfer = ResourceStack.merge(existing, Optional.of(stack), maxStackSize);
		if (!simulate && !(Objects.equal(existing, transfer.merged()))) {
			contents.set(slot, transfer.merged());
			fireChanged();
		}
		
		return transfer.rejected();
	}

	@Override
	public Optional<ResourceStack<T>> insert(ResourceStack<T> stack, boolean simulate) {
		Optional<ResourceStack<T>> remainder = Optional.of(stack);
		
		for(int i=0; i<contents.size(); i++) {
			ResourceStack.MergeResult<T> transfer = ResourceStack.merge(contents.get(i), remainder, maxStackSize);
			if (!simulate) {
				contents.set(i, transfer.merged());
			}
			remainder = transfer.rejected();
			if (remainder.isEmpty()) break;
		}
		
		if (!simulate) fireChanged();
		
		return remainder;
	}
	
	/*
	 * IMPLEMENTS ExtractCapability
	 */
	
	@Override
	public Optional<ResourceStack<T>> extract(int slot, long amount, boolean simulate) {
		if (slot < 0 || slot >= contents.size()) return Optional.empty();
		
		Optional<ResourceStack<T>> existing = contents.get(slot);
		if (existing.isEmpty()) return Optional.empty();
		
		ResourceStack.SplitResult<T> transfer = ResourceStack.split(existing, amount);
		
		if (!simulate) {
			contents.set(slot, transfer.remaining());
			fireChanged();
		}
		
		return transfer.split();
	}

	@Override
	public Optional<ResourceStack<T>> extract(Resource<T> resource, long amount, boolean simulate) {
		if (amount == 0) return Optional.empty();
		
		Optional<ResourceStack<T>> accumulated = Optional.empty();
		
		for(int i=0; i<contents.size(); i++) {
			long remaining = amount - ResourceStack.count(accumulated);
			if (remaining <= 0) break;
			
			Optional<ResourceStack<T>> cur = contents.get(i);
			if (!Objects.equal(cur, Optional.of(resource))) continue;
			
			ResourceStack.SplitResult<T> transfer = ResourceStack.split(cur, remaining);
			
			if (!simulate) {
				contents.set(i, transfer.remaining());
			}
			
			if (accumulated.isEmpty()) {
				accumulated = transfer.split();
			} else {
				ResourceStack<T> acc = accumulated.get();
				accumulated = acc.copyWithCount(acc.count() + ResourceStack.count(transfer.split()));
			}
			
			if (ResourceStack.count(accumulated) == amount) break;
		}
		
		// Something in the inventory is changing if and only if we're not in simulation mode, and
		// we have accumulated at least one item for extraction.
		if (!simulate && !accumulated.isEmpty()) {
			fireChanged();
		}
		
		return accumulated;
	}

	@Override
	public Optional<ResourceStack<T>> extract(T resource, long amount, boolean simulate) {
		if (amount == 0) return Optional.empty();
		
		Optional<ResourceStack<T>> accumulated = Optional.empty();
		
		for(int i=0; i<contents.size(); i++) {
			long remaining = amount - ResourceStack.count(accumulated);
			if (remaining <= 0) break;
			
			Optional<ResourceStack<T>> cur = contents.get(i);
			if (!Objects.equal(cur.map(ResourceStack::resource).map(Resource::object), Optional.of(resource))) continue;
			
			// We need to do a prospective extract-and-merge, and then compare the intermediate and result quantities
			ResourceStack.SplitResult<T> extract = ResourceStack.split(cur, remaining);
			ResourceStack.MergeResult<T> merge = ResourceStack.merge(accumulated, extract.split(), amount);
			// If we can't fully merge the stack, and it's not for quantity reasons, generally the components are incompatible, so skip this stack.
			if (!merge.rejected().isEmpty()) continue;
			
			if (!simulate) {
				contents.set(i, extract.remaining());
			}
			
			accumulated = merge.merged();
			
			if (ResourceStack.count(accumulated) == amount) break;
		}
		
		// Something in the inventory is changing if and only if we're not in simulation mode, and
		// we have accumulated at least one item for extraction.
		if (!simulate && !accumulated.isEmpty()) {
			fireChanged();
		}
		
		return accumulated;
	}
	
}
