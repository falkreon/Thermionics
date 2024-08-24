package blue.endless.thermionics.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class ThermalPipeBlock extends Block implements BlockEntityProvider {
	public static BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	public ThermalPipeBlock() {
		super(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK));
		
		this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		
		builder.add(WATERLOGGED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		// TODO Auto-generated method stub
		return null;
	}
}
