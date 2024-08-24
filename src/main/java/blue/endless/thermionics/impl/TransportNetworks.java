package blue.endless.thermionics.impl;

import blue.endless.thermionics.api.rotary.RotaryPower;
import net.minecraft.server.world.ServerWorld;

public class TransportNetworks {
	public static void tick(ServerWorld world) {
		RotaryPower.postTick(world);
	}
}
