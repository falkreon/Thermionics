package blue.endless.thermionics.block.entity;

import blue.endless.thermionics.block.ThermionicsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class MachineCasingBlockEntity extends BlockEntity {

	public MachineCasingBlockEntity(BlockPos pos, BlockState state) {
		super(ThermionicsBlocks.BLOCKENTITY_MACHINE_CASING, pos, state);
	}

}
