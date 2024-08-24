package blue.endless.thermionics.api.transfer.storage;

import java.util.Objects;

import blue.endless.thermionics.api.transfer.Transfer;
import blue.endless.thermionics.api.transfer.VariantStack;
import blue.endless.thermionics.api.transfer.capability.StorageCapability;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

public abstract class OneSlotStorage<T> implements StorageCapability<T> {
	
	protected abstract Transfer<T> getTransfer();
	
	@Override
	public VariantStack<T> insert(int slot, VariantStack<T> stack, boolean simulate) {
		if (slot != 0) return stack;
		
		return insert(stack, simulate);
	}

	@Override
	public VariantStack<T> extract(int slot, long amount, boolean simulate) {
		if (slot != 0) return getTransfer().blank();
		
		return extract(amount, simulate);
	}

	@Override
	public VariantStack<T> extract(TransferVariant<T> variant, long amount, boolean simulate) {
		if (!Objects.equals(getSlot(0).variant(), variant)) return getTransfer().blank();
		
		return extract(variant.getObject(), amount, simulate);
	}

	@Override
	public VariantStack<T> extract(T resource, long amount, boolean simulate) {
		if (!Objects.equals(getSlot(0).variant().getObject(), resource)) return getTransfer().blank();
		
		return extract(amount, simulate);
	}
	
	protected abstract VariantStack<T> extract(long amount, boolean simulate);

	@Override
	public VariantStack<T> getSlot(int slotNumber) {
		if (slotNumber == 0) return getTransfer().blank();
		return getStack();
	}
	
	protected abstract VariantStack<T> getStack();

	@Override
	public int getSlotCount() {
		return 1;
	}

}
