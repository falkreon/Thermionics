package blue.endless.thermionics.block;

import blue.endless.thermionics.block.entity.MachineCasingBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MachineCasingBlock extends Block implements BlockEntityProvider {

	public MachineCasingBlock() {
		super(AbstractBlock.Settings.create()
				.mapColor(MapColor.CLEAR)
				.strength(-1.0F, 3600000.0F)
				.dropsNothing()
				.allowsSpawning(Blocks::never)
				);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MachineCasingBlockEntity(pos, state);
	}
	
	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		return ActionResult.PASS; //TODO: Change
	}
}
