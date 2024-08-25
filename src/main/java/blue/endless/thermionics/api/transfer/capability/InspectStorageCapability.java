package blue.endless.thermionics.api.transfer.capability;

import blue.endless.thermionics.api.transfer.OptionalStack;

/**
 * Transfer capability class that allows the user to read the contents of each storage slot
 * @param <T> the resource type (e.g. Item, Fluid)
 */
public interface InspectStorageCapability<T> {
	
	/**
	 * Gets the contents of the specified slot
	 * @param slotNumber the slot to inspect
	 * @return the contents of the specified slot
	 */
	public OptionalStack<T> getSlot(int slotNumber);
	
	/**
	 * Gets the number of slots available. Slots are numbered from 0 to getSlotCount() - 1
	 * @return the number of slots available
	 */
	public int getSlotCount();
}
