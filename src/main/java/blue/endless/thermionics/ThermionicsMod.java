package blue.endless.thermionics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.endless.thermionics.api.RgbDye;
import blue.endless.thermionics.api.fluid.FluidComponents;
import blue.endless.thermionics.block.ThermionicsBlocks;
import blue.endless.thermionics.impl.TransportNetworks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;

public class ThermionicsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Thermionics");
	public static final String ID = "thermionics";
	
	@Override
	public void onInitialize() {
		RgbDye.init();
		FluidComponents.init();
		ThermionicsBlocks.init();
		
		ServerTickEvents.END_WORLD_TICK.register((serverWorld) -> {
			TransportNetworks.tick(serverWorld);
		});
	}
	
	public static Identifier id(String path) {
		return Identifier.of(ID, path);
	}
}

/*

Notes:

# Power Systems

## Heat

Heat is generated by Firebox and Fuel Rod.

It diffuses through "heat conductors"
It can also be transmitted at high efficiency at long distances via laser, but only in very large
packets that can often overwhelm a heat receiver.

## Rotary Power

Rotary power is generated by Turbine, which you need to turn with steam.
It can be transmitted with rods or belts.

## RF

Per TechReborn


*/