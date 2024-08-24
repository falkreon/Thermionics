package blue.endless.thermionics.api.transfer.capability;

import java.util.Optional;

import blue.endless.thermionics.api.transfer.ResourceStack;

public interface InsertCapability<T> {
	/**
	 * Attempts to insert the stack into a specific slot of this resource storage. If the slot does
	 * not exist, the entire stack will be rejected.
	 * @param slot the slot to merge the inserted stack into
	 * @param stack the stack to insert
	 * @param simulate if true, no changes will be committed
	 * @return a remainder stack of anything that could not be inserted
	 */
	public Optional<ResourceStack<T>> insert(int slot, ResourceStack<T> stack, boolean simulate);
	
	/**
	 * Attempts to insert the stack into this resource storage. The stack may be split across
	 * multiple storage slots. Anything that cannot be inserted will be returned.
	 * @param stack the stack to insert
	 * @param simulate if true, no changes will be committed
	 * @return a remainder stack of anything that could not be inserted
	 */
	public Optional<ResourceStack<T>> insert(ResourceStack<T> stack, boolean simulate);
}
