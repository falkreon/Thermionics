package blue.endless.thermionics.api.rotary;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RotaryPower {
	//TODO: Cache active power nets
	
	/**
	 * Deliver rotary power to connected axles and rotary consumers. Do not call more than once per
	 * tick. This device may not contribute power if the rotation speed / tick is already above the
	 * supplied revolutions
	 * @param world the world the device is in
	 * @param pos the position of the device that is supplying power
	 * @param torque the torque (turning force) supplied from this block
	 * @param revolutions the number of full crankshaft turns this device is contributing this tick
	 */
	public static void deliverPower(World world, BlockPos pos, int torque, double revolutions) {
		//TODO: Cache this information and process it in postTick
	}
	
	/**
	 * Internal method which commits rotary transport: adding up torque loads in a net and dividing
	 * supplied power between consumers.
	 * @param world
	 */
	@ApiStatus.Internal
	public static void postTick(ServerWorld world) {
		
	}
}
