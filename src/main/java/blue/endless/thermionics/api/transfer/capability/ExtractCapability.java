package blue.endless.thermionics.api.transfer.capability;

import blue.endless.thermionics.api.transfer.VariantStack;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

/**
 * Allows a user to extract items from this resource.
 * @param <T>
 */
public interface ExtractCapability<T> {
	/**
	 * Attempts to extract resources from a specific slot of this resource storage. Whatever is
	 * successfully extracted is returned. If the slot does not exist, a blank stack will be returned.
	 * @param slot the slot to extract from
	 * @param amount the maximum number of resources to extract
	 * @param simulate if true, no changes will be committed
	 * @return the resources that could be extracted
	 */
	public VariantStack<T> extract(int slot, long amount, boolean simulate);
	
	/**
	 * Attempts to extract a specific resource from this resource storage. Whatever is successfully
	 * extracted is returned. If the resource does not exist, a blank stack will be returned.
	 * Resources may come from multiple source stacks or slots in the storage.
	 * 
	 * <p>This method is sensitive to ComponentChanges. Only resources which match the exact
	 * TransferVariant will be returned.
	 * 
	 * @param variant the exact resource to extract
	 * @param amount the maximum number of resources to extract
	 * @param simulate if true, no changes will be committed
	 * @return the resources that could be extracted
	 */
	public VariantStack<T> extract(TransferVariant<T> variant, long amount, boolean simulate);
	
	/**
	 * Attempts to extract a specific resource from this resource storage. his method is NOT
	 * sensitive to ComponentChanges, and will grab any TransferVariant that matches, but resources
	 * from only one variant will be returned.
	 * 
	 * <p>Let's say we have a fluid storage container with three slots/tanks arranged like so:
	 * <ul>
	 *   <li>3 droplets WATER (no component changes)
	 *   <li>2 droplets WATER (no component changes)
	 *   <li>7 droplets WATER (component changes)
	 * </ul>
	 * 
	 * If you ask an ExtractCapability of this container for 10 droplets of WATER, you may get 5 or
	 * you may get 7. No guarantee is made that the largest stack possible is returned, and it will
	 * frequently depend on the order the resources are arranged in.
	 * 
	 * @param resource the resource to extract
	 * @param amount the maximum number of resources to extract
	 * @param simulate if true, no changes will be committed
	 * @return the resources that could be extracted
	 */
	public VariantStack<T> extract(T resource, long amount, boolean simulate);
}
