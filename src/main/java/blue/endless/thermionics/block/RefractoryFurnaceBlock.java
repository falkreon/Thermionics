package blue.endless.thermionics.block;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import blue.endless.thermionics.block.entity.RefractoryFurnaceEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class RefractoryFurnaceBlock extends Block implements BlockEntityProvider {

	public RefractoryFurnaceBlock() {
		super(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK));
		
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new RefractoryFurnaceEntity(pos, state);
	}
	
	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient()) return ActionResult.SUCCESS;
		
		// TODO: Assemble the Refractory Configuration
		
		Configuration conf = scan(world, pos);
		
		
		return super.onUse(state, world, pos, player, hit);
	}
	
	
	public static @Nullable Configuration scan(WorldAccess access, BlockPos controller) {
		BlockState controllerState = access.getBlockState(controller);
		if (!(controllerState.getBlock() instanceof RefractoryFurnaceBlock)) return null;
		
		//Find the axis of the wall
		
		boolean n = !access.isAir(controller.north());
		boolean s = !access.isAir(controller.south());
		boolean e = !access.isAir(controller.east());
		boolean w = !access.isAir(controller.west());
		
		boolean axisX = n && s;
		boolean axisZ = e && w;
		if (!(axisX ^ axisZ)) return null;
		// Okay, now that we've done validation we can build basis axes for the wall.
		// This is incomplete because we don't know which side is the "front", so we don't actually
		// have a normal *vector* yet.
		Direction.Axis wallAxis = (axisX) ? Direction.Axis.X : Direction.Axis.Z;
		Direction.Axis normalAxis = (axisX) ? Direction.Axis.Z : Direction.Axis.X;
		
		// If we see these variables set to these values later, please barf and create an error
		Direction normal = null;
		int floorY = controller.getY();
		
		for(int i=1; i<=16; i++) {
			BlockPos cur = controller.down(i);
			//We need to examine cur + normal and cur - normal
			//First time we hit a legal floor block, we stop and register a floor level and a normal Direction
			BlockPos curPlusAxis = cur.offset(normalAxis, 1);
			BlockPos curMinusAxis = cur.offset(normalAxis, -1);
			BlockState plusState = access.getBlockState(curPlusAxis);
			BlockState minusState = access.getBlockState(curMinusAxis);
			boolean face = plusState.isAir() && minusState.isAir();
		}
		
		
		return null;
	}
	
	public static record Configuration(
			int productBandwidth,
			BlockPos[] heaters,
			BlockPos[] intakes,
			BlockPos[] exhausts,
			BlockPos[] drains
		) {}
}
