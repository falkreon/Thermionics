package blue.endless.thermionics.api.transfer.storage;

import blue.endless.thermionics.api.transfer.OptionalStack;
import blue.endless.thermionics.api.transfer.Resource;
import blue.endless.thermionics.api.transfer.ResourceStack;
import blue.endless.thermionics.api.transfer.capability.StorageCapability;

public abstract class OneSlotStorage<T> implements StorageCapability<T> {
	
	@Override
	public OptionalStack<T> insert(int slot, ResourceStack<T> stack, boolean simulate) {
		if (slot != 0) return OptionalStack.of(stack);
		
		return insert(stack, simulate);
	}

	@Override
	public OptionalStack<T> extract(int slot, long amount, boolean simulate) {
		if (slot != 0) return OptionalStack.empty();
		
		return extract(amount, simulate);
	}

	@Override
	public OptionalStack<T> extract(Resource<T> resource, long amount, boolean simulate) {
		if (!getStack().isOf(resource)) return OptionalStack.empty();
		
		return extract(amount, simulate);
	}

	@Override
	public OptionalStack<T> extract(T resource, long amount, boolean simulate) {
		if (!getStack().isOf(resource)) return OptionalStack.empty();
		
		return extract(amount, simulate);
	}
	
	protected abstract OptionalStack<T> extract(long amount, boolean simulate);

	@Override
	public OptionalStack<T> getSlot(int slotNumber) {
		if (slotNumber == 0) return OptionalStack.empty();
		return getStack();
	}
	
	protected abstract OptionalStack<T> getStack();

	@Override
	public int getSlotCount() {
		return 1;
	}

}
