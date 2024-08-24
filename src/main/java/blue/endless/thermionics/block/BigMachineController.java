package blue.endless.thermionics.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BigMachineController extends Block implements BlockEntityProvider {

	public BigMachineController(Settings settings) {
		super(settings);
		
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		
		return null;
	}

}
