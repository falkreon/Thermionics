package blue.endless.thermionics.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class FuelRodBlock extends Block implements BlockEntityProvider {

	public FuelRodBlock() {
		super(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK));
		
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
