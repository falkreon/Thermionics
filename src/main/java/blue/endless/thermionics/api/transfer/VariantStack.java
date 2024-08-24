package blue.endless.thermionics.api.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

/**
 * Represents a resource and an amount. Like an ItemStack, or a FluidStack, or an EnergyPacket.
 * Except that thanks to the magic of Variants, we can make this behavior generic.
 * 
 * <p>VariantStacks, being records, are immutable. If you want to change a stack size, you need to
 * create a new record instance, such as using {@link #copyWithCount(long)}. A side benefit is that
 * <em>VariantStacks are threadsafe.</em> Once properly created, they can be passed freely between
 * threads, accessed by multiple threads, cached, serialized, etc.
 * 
 * <p>
 * 
 * @param <T> The Variant type (e.g. Fluid, Item)
 */
public record VariantStack<T>(TransferVariant<T> variant, long count) {
	
	public boolean isEmpty() {
		return variant.isBlank() || count <= 0;
	}
	
	public VariantStack<T> copyWithCount(long count) {
		return new VariantStack<>(this.variant, count);
	}
	
	/**
	 * Creates a Transfer API ResourceAmount representing identical data to this VariantStack.
	 * @return a ResourceAmount identical (in resource, components, and amouint) to this VariantStack
	 */
	public ResourceAmount<TransferVariant<T>> toResourceAmount() {
		return new ResourceAmount<>(variant, count);
	}
	
	/**
	 * Creates a VariantStack representing identical data to the provided ResourceAmount
	 * @param <T> The Variant type (e.g. Fluid, Item)
	 * @param resourceAmount the Transfer API ResourceAmount to make a VariantStack out of
	 * @return a VariantStack identical (in resource, components, and amount) to the ResourceAmount
	 */
	public static <T> VariantStack<T> of(ResourceAmount<TransferVariant<T>> resourceAmount) {
		return new VariantStack<>(resourceAmount.resource(), resourceAmount.amount());
	}
	
	public static VariantStack<Item> of(ItemStack itemStack) {
		return new VariantStack<>(ItemVariant.of(itemStack), itemStack.getCount());
	}
	
	public static VariantStack<Item> of(ItemConvertible item, long quantity) {
		return new VariantStack<>(ItemVariant.of(item), quantity);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack toItemStack(VariantStack<Item> variantStack) {
		if (variantStack.isEmpty()) return ItemStack.EMPTY;
		
		return new ItemStack(
				variantStack.variant.getObject().getRegistryEntry(),
				(int) variantStack.count, variantStack.variant.getComponents());
	}
}
