package blue.endless.thermionics.api.transfer.capability;

import org.jetbrains.annotations.Nullable;

import blue.endless.thermionics.ThermionicsMod;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.math.Direction;

/**
 * The overall transfer capability
 */
public interface TransferCapabilityProvider {
	public static final BlockApiLookup<TransferCapabilityProvider, Direction> LOOKUP = BlockApiLookup.get(ThermionicsMod.id("transfer_capability"), TransferCapabilityProvider.class, Direction.class);
	
	/**
	 * Returns true if pipes or devices which deal in this resourceType should visually connect to
	 * this device. This is purely visual feedback to the player that they have placed blocks
	 * correctly, and not an indication of any particular behavior 
	 * @param resourceType
	 * @return
	 */
	public default boolean shouldConnect(Class<?> resourceType) {
		return getTransferCapability(resourceType) != null;
	}
	
	@Nullable
	public default <T> TransferCapability<T> getTransferCapability(Class<T> resourceType) {
		return getStorageCapability(resourceType);
	}
	
	@Nullable
	public default <T> InspectStorageCapability<T> getInspectStorageCapability(Class<T> resourceType) {
		return getStorageCapability(resourceType);
	}
	
	@Nullable
	public default <T> ShoppingListCapability getShoppingListCapability() {
		return null;
	}
	
	@Nullable
	public <T> StorageCapability<T> getStorageCapability(Class<T> resourceType);
}
