package blue.endless.thermionics.block.entity;

import blue.endless.thermionics.block.ThermionicsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class RefractoryFurnaceEntity extends BlockEntity {

	public RefractoryFurnaceEntity(BlockPos pos, BlockState state) {
		super(ThermionicsBlocks.BLOCKENTITY_REFRACTORY_FURNACE, pos, state);
	}

}
