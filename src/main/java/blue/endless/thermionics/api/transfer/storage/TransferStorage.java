package blue.endless.thermionics.api.transfer.storage;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.include.com.google.common.base.Objects;

import blue.endless.thermionics.api.transfer.OptionalStack;
import blue.endless.thermionics.api.transfer.Resource;
import blue.endless.thermionics.api.transfer.ResourceStack;
import blue.endless.thermionics.api.transfer.capability.StorageCapability;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.collection.DefaultedList;

public class TransferStorage<T> implements StorageCapability<T> {
	private final long maxStackSize;
	private DefaultedList<OptionalStack<T>> contents;
	private List<Runnable> changeListeners = new ArrayList<>();
	
	public TransferStorage(int slots, long maxStackSize) {
		contents = DefaultedList.ofSize(slots, OptionalStack.empty());
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
	public OptionalStack<T> getSlot(int slotNumber) {
		if (slotNumber < 0 || slotNumber >= contents.size()) return OptionalStack.empty();
		
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
	public OptionalStack<T> insert(int slot, ResourceStack<T> stack, boolean simulate) {
		if (slot < 0 || slot >= contents.size()) return OptionalStack.of(stack);
		OptionalStack<T> existing = getSlot(slot);
		
		ResourceStack.MergeResult<T> transfer = existing.merge(stack, maxStackSize);
		if (!simulate && !(Objects.equal(existing, transfer.merged()))) {
			contents.set(slot, transfer.merged());
			fireChanged();
		}
		
		return transfer.rejected();
	}

	@Override
	public OptionalStack<T> insert(ResourceStack<T> stack, boolean simulate) {
		OptionalStack<T> remainder = OptionalStack.of(stack);
		
		for(int i=0; i<contents.size(); i++) {
			ResourceStack.MergeResult<T> transfer = contents.get(i).merge(remainder, maxStackSize);
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
	public OptionalStack<T> extract(int slot, long amount, boolean simulate) {
		if (slot < 0 || slot >= contents.size()) return OptionalStack.empty();
		
		OptionalStack<T> existing = contents.get(slot);
		if (existing.isEmpty()) return OptionalStack.empty();
		
		ResourceStack.SplitResult<T> transfer = existing.split(amount);
		
		if (!simulate) {
			contents.set(slot, transfer.remaining());
			fireChanged();
		}
		
		return transfer.split();
	}

	@Override
	public OptionalStack<T> extract(Resource<T> resource, long amount, boolean simulate) {
		if (amount == 0) return OptionalStack.empty();
		
		OptionalStack<T> accumulated = OptionalStack.empty();
		
		for(int i=0; i<contents.size(); i++) {
			long remaining = amount - accumulated.count();
			if (remaining <= 0) break;
			
			OptionalStack<T> cur = contents.get(i);
			if (!cur.isOf(resource)) continue;
			
			ResourceStack.SplitResult<T> transfer = cur.split(remaining);
			
			if (!simulate) {
				contents.set(i, transfer.remaining());
			}
			
			if (accumulated.isEmpty()) {
				accumulated = transfer.split();
			} else {
				ResourceStack<T> acc = accumulated.get();
				accumulated = acc.copyWithCount(acc.count() + transfer.split().count());
			}
			
			if (accumulated.count() == amount) break;
		}
		
		// Something in the inventory is changing if and only if we're not in simulation mode, and
		// we have accumulated at least one item for extraction.
		if (!simulate && !accumulated.isEmpty()) {
			fireChanged();
		}
		
		return accumulated;
	}

	@Override
	public OptionalStack<T> extract(T resource, long amount, boolean simulate) {
		if (amount == 0) return OptionalStack.empty();
		
		OptionalStack<T> accumulated = OptionalStack.empty();
		
		for(int i=0; i<contents.size(); i++) {
			long remaining = amount - accumulated.count();
			if (remaining <= 0) break;
			
			OptionalStack<T> cur = contents.get(i);
			
			if (!cur.isOf(resource)) continue;
			
			// We need to do a prospective extract-and-merge, and then compare the intermediate and result quantities
			ResourceStack.SplitResult<T> extract = cur.split(remaining);
			ResourceStack.MergeResult<T> merge = accumulated.merge(extract.split(), amount);
			// If we can't fully merge the stack, and it's not for quantity reasons, generally the components are incompatible, so skip this stack.
			if (merge.rejected().isPresent()) continue;
			
			if (!simulate) {
				contents.set(i, extract.remaining());
			}
			
			accumulated = merge.merged();
			
			if (accumulated.count() == amount) break;
		}
		
		// Something in the inventory is changing if and only if we're not in simulation mode, and
		// we have accumulated at least one item for extraction.
		if (!simulate && accumulated.isPresent()) {
			fireChanged();
		}
		
		return accumulated;
	}
	
}
