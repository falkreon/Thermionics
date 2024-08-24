package blue.endless.thermionics.api.fluid;

import java.util.Optional;
import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;

/*
 * Impl notes: Unfortunately, while these components would be super great, FluidHandler exists on FluidVariant instead.
 * It's unclear whether there's any way to shim these components in as defaults, and I'm currently thinking about what
 * to do with them instead - whether to delete this class, for example.
 */

public class FluidComponents {
	private static final Codec<Optional<SoundEvent>> OPTIONAL_SOUND_EVENT_CODEC = Codecs.optional(SoundEvent.CODEC);
	private static final PacketCodec<ByteBuf, Optional<SoundEvent>> OPTIONAL_SOUND_EVENT_PACKET_CODEC = SoundEvent.PACKET_CODEC.collect(PacketCodecs::optional);
	
	/**
	 * The display name that for the fluid.
	 */
	public static final ComponentType<Text> NAME = register("name", builder -> builder.codec(TextCodecs.CODEC).packetCodec(TextCodecs.PACKET_CODEC));
	
	/**
	 * The sound corresponding to this fluid being filled, or none if no sound is available.
	 */
	public static final ComponentType<Optional<SoundEvent>> FILL_SOUND = register("fill_sound",
			builder -> builder.codec(OPTIONAL_SOUND_EVENT_CODEC).packetCodec(OPTIONAL_SOUND_EVENT_PACKET_CODEC));
	
	/**
	 * The sound corresponding to this fluid being emptied, or none if no sound is available.
	 */
	
	public static final ComponentType<Optional<SoundEvent>> EMPTY_SOUND = register("empty_sound",
			builder -> builder.codec(OPTIONAL_SOUND_EVENT_CODEC).packetCodec(OPTIONAL_SOUND_EVENT_PACKET_CODEC));
	
	/**
	 * An integer in [0, 15]: the light level emitted by this fluid, or 0 if it doesn't naturally emit light.
	 */
	public static final ComponentType<Integer> LUMINANCE = register("luminance", builder -> builder.codec(Codecs.rangedInt(0, 15)).packetCodec(PacketCodecs.VAR_INT));
	
	/**
	 * A non-negative integer, representing the temperature of this fluid in Kelvin.
	 * The reference values are {@value FluidConstants#WATER_TEMPERATURE} for water, and {@value FluidConstants#LAVA_TEMPERATURE} for lava.
	 */
	public static final ComponentType<Integer> TEMPERATURE = register("temperature", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
	
	/**
	 * A positive integer, representing the viscosity of this fluid.
	 * Fluids with lower viscosity generally flow faster than fluids with higher viscosity.
	 * @see {@link
	 *      net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler#getViscosity(net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant, net.minecraft.world.World)
	 *      FluidVariantAttributeHandler::getViscosity
	 *      }
	 */
	public static final ComponentType<Integer> VISCOSITY = register("viscosity", builder -> builder.codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.VAR_INT));
	
	/**
	 * True if this fluid is lighter than air.
	 * Fluids that are lighter than air generally flow upwards.
	 */
	public static final ComponentType<Boolean> IS_LIGHTER_THAN_AIR = register("is_lighter_than_air", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));
	
	
	public static void init() {
		// Dummy method, could also use Class.forName
	}
	
	public static ComponentMap forFluid(Fluid fluid) {
		FluidVariantAttributeHandler handler = FluidVariantAttributes.getHandlerOrDefault(fluid);
		FluidVariant variant = FluidVariant.of(fluid);
		return ComponentMap.builder()
			.add(NAME, handler.getName(variant))
			.add(FILL_SOUND, handler.getFillSound(variant))
			.add(EMPTY_SOUND, handler.getEmptySound(variant))
			.add(LUMINANCE, handler.getLuminance(variant))
			.add(TEMPERATURE, handler.getTemperature(variant))
			.add(VISCOSITY, handler.getViscosity(variant, null))
			.add(IS_LIGHTER_THAN_AIR, handler.isLighterThanAir(variant))
			.build();
	}
	
	private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, id, ((ComponentType.Builder<T>)builderOperator.apply(ComponentType.builder())).build());
	}
}
