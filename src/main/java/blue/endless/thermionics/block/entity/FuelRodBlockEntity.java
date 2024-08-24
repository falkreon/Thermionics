package blue.endless.thermionics.block.entity;

import blue.endless.thermionics.block.ThermionicsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class FuelRodBlockEntity extends BlockEntity {

	public FuelRodBlockEntity(BlockPos pos, BlockState state) {
		super(ThermionicsBlocks.BLOCKENTITY_FUEL_ROD, pos, state);
		
	}

}
