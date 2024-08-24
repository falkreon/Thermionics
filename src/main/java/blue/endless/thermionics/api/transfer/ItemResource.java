package blue.endless.thermionics.api.transfer;

import java.util.Optional;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Helper class for working with Item Resources and ResourceStacks.
 */
public class ItemResource {
	
	/**
	 * Creates a validated Resource representing the specified Item with the specified ComponentChanges
	 * @param item the item to make a Resource from. MUST NOT be air.
	 * @param components The componentChanges to include
	 * @return the Item Resource
	 */
	public static Resource<Item> validated(Item item, ComponentChanges components) {
		if (item == Items.AIR) throw new IllegalArgumentException("Concrete item resources cannot be air (empty).");
		
		return new Resource<>(item, components);
	}
	
	/**
	 * Creates a validated Resource representing the specified Item with no components
	 * @param item the item to make a Resource from. MUST NOT be air or structure void.
	 * @return the Item Resource
	 */
	public static Resource<Item> validated(Item item) {
		return validated(item, ComponentChanges.EMPTY);
	}
	
	public static Optional<Resource<Item>> of(Item item, ComponentChanges components) {
		if (item == Items.AIR) return Optional.empty();
		
		return Optional.of(new Resource<>(item, components));
	}
	
	/**
	 * Creates a ResourceStack equivalent to this ItemStack. Returns Optional.empty if this ItemStack
	 * is empty.
	 * @param stack The ItemStack to convert
	 * @return a ResourceStack representing this ItemStack, or empty if this stack is empty
	 */
	public static Optional<ResourceStack<Item>> stack(ItemStack stack) {
		if (stack.isEmpty()) return Optional.empty();
		
		return Optional.of(
				new ResourceStack<>(
						validated(stack.getItem(), stack.getComponentChanges()),
						stack.getCount()
				)
		);
	}
	
	/**
	 * Create a validated, non-Optional Item ResourceStack.
	 * @param item the Item. MUST not be AIR
	 * @param components the component changes
	 * @param count the number of resources. MUST be positive (cannot be zero)
	 * @return the ResourceStack
	 */
	public static ResourceStack<Item> validatedStack(Item item, ComponentChanges components, long count) {
		if (count <= 0L) throw new IllegalArgumentException("Count cannot be zero or negative (was "+count+")");
		return new ResourceStack<>(validated(item, components), count);
	}
	
	/**
	 * Create a validated, non-Optional Item ResourceStack.
	 * @param item the Item. MUST not be AIR
	 * @param count the number of items. MUST be positive (cannot be zero)
	 * @return the ResourceStack
	 */
	public static ResourceStack<Item> validatedStack(Item item, long count) {
		return validatedStack(item, ComponentChanges.EMPTY, count);
	}
	
	/**
	 * Create an Item ResourceStack from the provided arguments, or empty if blank/empty info is provided.
	 * @param item the item. AIR is permitted and will result in empty being returned.
	 * @param components the component changes
	 * @param count the number of items. Zero or negative counts are permitted, and will result in empty being returned.
	 * @return The Item ResourceStack, or empty if the arguments describe an empty stack
	 */
	public static Optional<ResourceStack<Item>> stack(Item item, ComponentChanges components, long count) {
		if (count <= 0L) return Optional.empty();
		return ItemResource.of(item, components).map(it -> new ResourceStack<>(it, count));
	}
	
	
	/**
	 * Converts from a ResourceTransferAPI resource to a fabric-transfer-api ItemVariant.
	 * @param resource the componentized Item from this API
	 * @return a fabric-transfer-api object equivalent to this Resource
	 */
	public static ItemVariant toVariant(Resource<Item> resource) {
		return ItemVariant.of(resource.object(), resource.components());
	}
	
	/**
	 * Converts from an Item TransferVariant, such as an ItemVariant, into an Item Resource from this API.
	 * @param variant the ItemVariant to convert into a Resource
	 * @return The Item Resource equivalent of the provided ItemVariant, or empty if the variant is blank
	 */
	public static Optional<Resource<Item>> toResource(TransferVariant<Item> variant) {
		if (variant.isBlank()) return Optional.empty();
		return Optional.of(new Resource<Item>(variant.getObject(), variant.getComponents()));
	}
	
	/**
	 * Converts from a ResourceStack from this API into a ResourceAmount for transfer-api.
	 * @param resourceStack the ResourceStack
	 * @return the equivalent ResourceAmount
	 */
	public static ResourceAmount<ItemVariant> toTransferApi(ResourceStack<Item> resourceStack) {
		return new ResourceAmount<>(toVariant(resourceStack.resource()), resourceStack.count());
	}
	
}
