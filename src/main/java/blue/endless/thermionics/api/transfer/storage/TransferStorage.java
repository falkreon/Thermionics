package blue.endless.thermionics.api.transfer.storage;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.include.com.google.common.base.Objects;

import blue.endless.thermionics.api.transfer.ShoppingList;
import blue.endless.thermionics.api.transfer.Transfer;
import blue.endless.thermionics.api.transfer.VariantStack;
import blue.endless.thermionics.api.transfer.capability.ShoppingListCapability;
import blue.endless.thermionics.api.transfer.capability.StorageCapability;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.collection.DefaultedList;

public class TransferStorage<T> implements StorageCapability<T>, ShoppingListCapability {
	private final long maxStackSize;
	private final Transfer<T> transferBuddy;
	private DefaultedList<VariantStack<T>> contents;
	private List<Runnable> changeListeners = new ArrayList<>();
	
	public TransferStorage(int slots, long maxStackSize, Transfer<T> transfer) {
		contents = DefaultedList.ofSize(slots, transfer.blank());
		this.maxStackSize = maxStackSize;
		this.transferBuddy = transfer;
	}
	
	public static TransferStorage<Item> items(int slots) {
		return new TransferStorage<>(slots, 64, Transfer.ITEM);
	}
	
	public static TransferStorage<Item> singleItemStack() {
		return new TransferStorage<>(1, 64, Transfer.ITEM);
	}
	
	public static TransferStorage<Fluid> fluids(int tanks, long tankCapacity) {
		return new TransferStorage<>(tanks, tankCapacity, Transfer.FLUID);
	}
	
	public static TransferStorage<Fluid> fluidTank(long tankCapacity) {
		return new TransferStorage<>(1, tankCapacity, Transfer.FLUID);
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
	public VariantStack<T> getSlot(int slotNumber) {
		if (slotNumber < 0 || slotNumber >= contents.size()) return transferBuddy.blank();
		
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
	public VariantStack<T> insert(int slot, VariantStack<T> stack, boolean simulate) {
		if (slot < 0 || slot >= contents.size()) return stack;
		VariantStack<T> existing = getSlot(slot);
		
		Transfer.Result<T> transfer = transferBuddy.merge(existing, stack, maxStackSize);
		if (!simulate) {
			contents.set(slot, transfer.result());
		}
		
		if (!simulate) fireChanged();
		
		return transfer.rejected();
	}

	@Override
	public VariantStack<T> insert(VariantStack<T> stack, boolean simulate) {
		VariantStack<T> remainder = stack;
		
		for(int i=0; i<contents.size(); i++) {
			Transfer.Result<T> transfer = transferBuddy.merge(contents.get(i), remainder, maxStackSize);
			if (!simulate) {
				contents.set(i, transfer.result());
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
	public VariantStack<T> extract(int slot, long amount, boolean simulate) {
		if (slot < 0 || slot >= contents.size()) return transferBuddy.blank();
		
		VariantStack<T> existing = contents.get(slot);
		if (existing.isEmpty()) return transferBuddy.blank();
		
		Transfer.Result<T> transfer = transferBuddy.split(existing, amount);
		
		if (!simulate) {
			contents.set(slot, transfer.rejected());
			fireChanged();
		}
		
		return transfer.result();
	}

	@Override
	public VariantStack<T> extract(TransferVariant<T> variant, long amount, boolean simulate) {
		if (amount == 0) return transferBuddy.blank();
		
		VariantStack<T> accumulated = transferBuddy.blank();
		
		for(int i=0; i<contents.size(); i++) {
			long remaining = amount - accumulated.count();
			if (remaining <= 0) break;
			
			VariantStack<T> cur = contents.get(i);
			if (!Objects.equal(cur.variant(), variant)) continue;
			
			Transfer.Result<T> transfer = transferBuddy.split(cur, remaining);
			
			if (!simulate) {
				contents.set(i, transfer.rejected());
			}
			
			if (accumulated.isEmpty()) {
				accumulated = transfer.result();
			} else {
				accumulated = accumulated.copyWithCount(accumulated.count() + transfer.result().count());
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
	public VariantStack<T> extract(T resource, long amount, boolean simulate) {
		if (amount == 0) return transferBuddy.blank();
		
		VariantStack<T> accumulated = transferBuddy.blank();
		
		for(int i=0; i<contents.size(); i++) {
			long remaining = amount - accumulated.count();
			if (remaining <= 0) break;
			
			VariantStack<T> cur = contents.get(i);
			if (!Objects.equal(cur.variant().getObject(), resource)) continue;
			
			// We need to do a prospective extract-and-merge, and then compare the intermediate and result quantities
			Transfer.Result<T> extract = transferBuddy.split(cur, remaining);
			Transfer.Result<T> merge = transferBuddy.merge(accumulated, extract.result(), amount);
			// If we can't fully merge the stack, and it's not for quantity reasons, generally the components are incompatible, so skip this stack.
			if (!merge.rejected().isEmpty()) continue;
			
			if (!simulate) {
				contents.set(i, extract.rejected());
			}
			
			accumulated = merge.result();
			
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
	public boolean extract(ShoppingList list, boolean simulate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean extractPartial(ShoppingList list, boolean simulate) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
