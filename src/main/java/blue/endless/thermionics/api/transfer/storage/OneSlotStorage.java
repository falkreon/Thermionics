package blue.endless.thermionics.api.transfer.storage;

import java.util.Optional;

import blue.endless.thermionics.api.transfer.Resource;
import blue.endless.thermionics.api.transfer.ResourceStack;
import blue.endless.thermionics.api.transfer.capability.StorageCapability;

public abstract class OneSlotStorage<T> implements StorageCapability<T> {
	
	@Override
	public Optional<ResourceStack<T>> insert(int slot, ResourceStack<T> stack, boolean simulate) {
		if (slot != 0) return Optional.of(stack);
		
		return insert(stack, simulate);
	}

	@Override
	public Optional<ResourceStack<T>> extract(int slot, long amount, boolean simulate) {
		if (slot != 0) return Optional.empty();
		
		return extract(amount, simulate);
	}

	@Override
	public Optional<ResourceStack<T>> extract(Resource<T> resource, long amount, boolean simulate) {
		if (getSlot(0).filter(it -> it.isOf(resource)).isEmpty()) return Optional.empty();
		
		return extract(amount, simulate);
	}

	@Override
	public Optional<ResourceStack<T>> extract(T resource, long amount, boolean simulate) {
		if (getSlot(0).filter(it -> it.isOf(resource)).isEmpty()) return Optional.empty();
		
		return extract(amount, simulate);
	}
	
	protected abstract Optional<ResourceStack<T>> extract(long amount, boolean simulate);

	@Override
	public Optional<ResourceStack<T>> getSlot(int slotNumber) {
		if (slotNumber == 0) return Optional.empty();
		return getStack();
	}
	
	protected abstract Optional<ResourceStack<T>> getStack();

	@Override
	public int getSlotCount() {
		return 1;
	}

}
