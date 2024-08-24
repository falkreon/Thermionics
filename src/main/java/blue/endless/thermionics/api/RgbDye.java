package blue.endless.thermionics.api;

import blue.endless.thermionics.ThermionicsMod;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.component.ComponentType;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;

/**
 * Objects of this interface can report dye properties on behalf of ItemStacks, such as its RGB
 * color, and the remainder it leaves behind when the dye is used.
 * 
 * <p>To make an item that will count as dye, just add an RgbDye.COMPONENT value to your
 * Item.Settings with the color or default color of that particular item, and then use
 * ComponentChanges on an ItemStack with that same component to make per-stack colors. If you want
 * to supply an empty container item after the dye has been used, have your Item implement RgbDye
 * directly and override {@link #getDyeRemainder(ItemStack)}.
 * 
 * <p>To use this API, use {@link #LOOKUP}:
 * <p>
 * <pre>{@code
 * RgbDye dyeHandler = RgbDye.LOOKUP.find(stack, null);
 * int dyeColor = dyeHandler.getDyeColor(stack);
 * ItemStack remainderItem = dyeHandler.getDyeRemainder(stack);
 * 
 * // From here, you would perform the rest of your interaction with the Container in question,
 * // informed by the method calls made here.
 * }</pre>
 */
public interface RgbDye {
	
	/**
	 * ItemApiLookup suitable for obtaining instances of RgbDye for an item.
	 */
	public static ItemApiLookup<RgbDye, Void> LOOKUP = ItemApiLookup.get(ThermionicsMod.id("rgb_dye"), RgbDye.class, Void.class);
	
	/**
	 * This represents an opaque RGB dye color. The presence of this Component on an item should be
	 * taken as permission to treat the item as consumable dye, even if API lookup can't produce an
	 * RgbDye object for it.
	 */
	public static ComponentType<Integer> COMPONENT = ComponentType.<Integer>builder()
			.codec(Codecs.rangedInt(0, 0xFFFFFF))
			.packetCodec(PacketCodecs.VAR_INT)
			.build();
	
	/**
	 * This represents a dyed color. The presence of this Componet DOES NOT indicate that this item
	 * is a dye; rather, that this item is dyable or has been dyed, and this is its current color.
	 */
	public static ComponentType<Integer> DYED_OBJECT_COMPONENT = ComponentType.<Integer>builder()
			.codec(Codecs.rangedInt(0, 0xFFFFFF))
			.packetCodec(PacketCodecs.VAR_INT)
			.build();

	/**
	 * Gets the opaque dye color that this stack contains
	 * @param stack the stack to get the color of
	 * @return the color of the provided ItemStack
	 */
	public default int getDyeColor(ItemStack stack) {
		Integer componentValue = stack.get(COMPONENT);
		return (componentValue == null) ? 0xFF_FFFFFF : componentValue | 0xFF_000000;
	}
	
	/**
	 * Gets the remnant item that should be left behind when an item is consumed for its RGB dye
	 * @return an ItemStack representing the empty dye container, or EMPTY if this item should be consumed.
	 */
	public default ItemStack getDyeRemainder(ItemStack stack) {
		return ItemStack.EMPTY;
	}
	
	public static RgbDye forVanillaDye(DyeItem item) {
		return new DefaultDyeColor(item.getColor().getEntityColor());
	}
	
	public static class DefaultDyeColor implements RgbDye {
		private final int color;
		
		public DefaultDyeColor(int color) {
			this.color = color & 0xFF_000000;
		}
		
		@Override
		public int getDyeColor(ItemStack stack) {
			Integer componentValue = stack.get(RgbDye.COMPONENT);
			return (componentValue == null) ? color : componentValue | 0xFF_000000;
		}
	}
	
	public static void init() {
		Registry.register(Registries.DATA_COMPONENT_TYPE, "dye_color", COMPONENT);
		
		LOOKUP.registerFallback((stack, ignored) -> {
			if (stack.getItem() instanceof DyeItem dyeItem) {
				return forVanillaDye(dyeItem);
			} else if (stack.getItem() instanceof RgbDye rgbDye) {
				return rgbDye;
			} else if (stack.get(COMPONENT) != null) {
				return new RgbDye() {};
			} else {
				return null;
			}
		});
	}
}
