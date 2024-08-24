package blue.endless.thermionics.block;

import blue.endless.thermionics.block.entity.VentBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VentBlock extends Block implements BlockEntityProvider {

	public VentBlock() {
		super(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK));
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new VentBlockEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if (type != ThermionicsBlocks.BLOCKENTITY_VENT) return null;
		
		return (World w, BlockPos pos, BlockState blockState, T be) -> {
			if (be instanceof VentBlockEntity vbe) {
				vbe.tick(w, pos, blockState);
			}
		};
		
	}
}
