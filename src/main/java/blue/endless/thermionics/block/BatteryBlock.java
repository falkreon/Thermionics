package blue.endless.thermionics.block;

import blue.endless.thermionics.ThermionicsMod;
import blue.endless.thermionics.block.entity.BatteryBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BatteryBlock extends Block implements BlockEntityProvider, Equipment, Identified {
	private final double PX = 1/16.0;
	
	VoxelShape SHAPE =
			VoxelShapes.union(
				VoxelShapes.union(
					VoxelShapes.cuboid( 1 * PX,  0 * PX,  1 * PX, 15 * PX, 11 * PX, 15 * PX),
					VoxelShapes.cuboid( 3 * PX, 11 * PX,  3 * PX, 13 * PX, 14 * PX, 13 * PX)
				),
				VoxelShapes.cuboid( 2 * PX, 13 * PX,  2 * PX, 14 * PX, 14 * PX, 14 * PX)
			);
	
	private final boolean creative;
	
	public BatteryBlock(boolean creative) {
		super(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK));
		this.creative = creative;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BatteryBlockEntity(pos, state);
	}
	
	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public EquipmentSlot getSlotType() {
		return EquipmentSlot.HEAD;
	}
	
	public boolean isCreative() {
		return this.creative;
	}

	@Override
	public Identifier getid() {
		return (creative) ? ThermionicsMod.id("creative_battery") : ThermionicsMod.id("battery");
	}
}
