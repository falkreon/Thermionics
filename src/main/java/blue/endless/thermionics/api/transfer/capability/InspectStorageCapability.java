package blue.endless.thermionics.api.transfer.capability;

import java.util.Optional;

import blue.endless.thermionics.api.transfer.ResourceStack;

/**
 * Transfer capability class that allows the user to read the contents of each storage slot for this
 * resource.
 * @param <T> the resource type (e.g. Item, Fluid)
 */
public interface InspectStorageCapability<T> {
	public Optional<ResourceStack<T>> getSlot(int slotNumber);
	public int getSlotCount();
}
