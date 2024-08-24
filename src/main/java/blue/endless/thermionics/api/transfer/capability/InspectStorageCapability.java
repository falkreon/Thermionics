package blue.endless.thermionics.api.transfer.capability;

import blue.endless.thermionics.api.transfer.VariantStack;

/**
 * Transfer capability class that allows the user to read the contents of each storage slot for this
 * resource.
 * @param <T> the resource type (e.g. Item, Fluid)
 */
public interface InspectStorageCapability<T> {
	public VariantStack<T> getSlot(int slotNumber);
	public int getSlotCount();
}
