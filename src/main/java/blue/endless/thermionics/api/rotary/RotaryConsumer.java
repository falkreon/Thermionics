package blue.endless.thermionics.api.rotary;

import blue.endless.thermionics.ThermionicsMod;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface RotaryConsumer {
	
	BlockApiLookup<RotaryConsumer, Void> LOOKUP = BlockApiLookup.get(ThermionicsMod.id("rotary_consumer"), RotaryConsumer.class, Void.class);
	
	/**
	 * Gets the torque load imposed by this device. Torque from all consumers is added up to find a
	 * torque load on the system, which is then split between consumers. Torque is also used to
	 * calculate stress on axles.
	 * @param World the world the device is in
	 * @param BlockPos the position of the device
	 * @return the minimum torque required to create any motion in this device.
	 */
	public int getTorqueLoad(World world, BlockPos pos);
	
	/**
	 * Supplies rotations to this device. Torque should be merely informative; no revolutions will
	 * be supplied if this machine's load were more than the turbines could handle.
	 * @param World the world the device is in
	 * @param BlockPos the position of the device
	 * @param torque the actual torque this device is experiencing
	 * @param revolutions the amount of power delivered, in whole 360-degree turns of the crankshaft
	 */
	public void supplyRotation(World world, BlockPos pos, int torque, double revolutions);
}
