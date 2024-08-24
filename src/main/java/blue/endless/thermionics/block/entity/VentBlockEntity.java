package blue.endless.thermionics.block.entity;

import blue.endless.thermionics.block.ThermionicsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VentBlockEntity extends BlockEntity {
	
	private FluidState fluidState = Fluids.EMPTY.getDefaultState();
	
	public VentBlockEntity(BlockPos pos, BlockState state) {
		super(ThermionicsBlocks.BLOCKENTITY_VENT, pos, state);
	}
	
	public FluidState getFluidState() {
		return fluidState;
	}

	public void tick(World world, BlockPos blockPos, BlockState blockState) {
		
	}
	
}
