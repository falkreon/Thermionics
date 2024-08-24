package blue.endless.thermionics.api;

import blue.endless.thermionics.ThermionicsMod;
import net.minecraft.util.Identifier;

/**
 * A MassResource is a resource normally only tracked by quantity, such as heat or energy.
 */
public record MassResource(Identifier id) {
	public static MassResource HEAT = new MassResource(ThermionicsMod.id("heat"));
}
