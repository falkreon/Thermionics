package blue.endless.thermionics.block;

import java.util.ArrayList;
import java.util.List;

import blue.endless.thermionics.ThermionicsMod;
import blue.endless.thermionics.block.entity.BatteryBlockEntity;
import blue.endless.thermionics.block.entity.FuelRodBlockEntity;
import blue.endless.thermionics.block.entity.MachineCasingBlockEntity;
import blue.endless.thermionics.block.entity.RefractoryFurnaceEntity;
import blue.endless.thermionics.block.entity.ThermalPipeBlockEntity;
import blue.endless.thermionics.block.entity.VentBlockEntity;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ThermionicsBlocks {
	
	public static BlockEntityType<FuelRodBlockEntity> BLOCKENTITY_FUEL_ROD;
	public static BlockEntityType<BatteryBlockEntity> BLOCKENTITY_BATTERY;
	public static BlockEntityType<RefractoryFurnaceEntity> BLOCKENTITY_REFRACTORY_FURNACE;
	public static BlockEntityType<MachineCasingBlockEntity> BLOCKENTITY_MACHINE_CASING;
	
	public static BlockEntityType<VentBlockEntity> BLOCKENTITY_VENT;
	public static BlockEntityType<ThermalPipeBlockEntity> BLOCKENTITY_THERMAL_PIPE;
	
	public static final List<ItemStack> machineBlocks = new ArrayList<>();
	
	public static void init() {
		// Block but no item - this is a bookkeeping block
		//Block machineCasingBlock = new MachineCasingBlock();
		//Registry.register(Registries.BLOCK, ThermionicsMod.id("machine_casing"), machineCasingBlock);
		//BLOCKENTITY_MACHINE_CASING = BlockEntityType.Builder.create(MachineCasingBlockEntity::new, machineCasingBlock).build();
		//Registry.register(Registries.BLOCK_ENTITY_TYPE, ThermionicsMod.id("machine_casing"), BLOCKENTITY_MACHINE_CASING);
		
		
		BLOCKENTITY_FUEL_ROD = machineBlock(
				ThermionicsMod.id("fuel_rod"),
				FuelRodBlockEntity::new,
				new FuelRodBlock()
				);
		
		BLOCKENTITY_BATTERY = machineBlocks(
				ThermionicsMod.id("battery"),
				BatteryBlockEntity::new,
				new BatteryBlock(false),
				new BatteryBlock(true)
				);
		
		BLOCKENTITY_VENT = machineBlock(
				ThermionicsMod.id("vent"),
				VentBlockEntity::new,
				new VentBlock()
				);
		
		BLOCKENTITY_THERMAL_PIPE = machineBlock(
				ThermionicsMod.id("lead_pipe"),
				ThermalPipeBlockEntity::new,
				new ThermalPipeBlock()
				);
		
		machineWithoutEntity(ThermionicsMod.id("refractory_firepit"), new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
		machineWithoutEntity(ThermionicsMod.id("refractory_brick"), new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
		//machineWithoutEntity(ThermionicsMod.id("refractory_vent"), new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
		machineWithoutEntity(ThermionicsMod.id("refractory_drain"), new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
		
		BLOCKENTITY_REFRACTORY_FURNACE = machineBlock(
				ThermionicsMod.id("refractory_furnace"),
				RefractoryFurnaceEntity::new,
				new RefractoryFurnaceBlock()
				);
		
		
		RegistryKey<ItemGroup> itemKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(ThermionicsMod.ID, "machines"));
		ItemGroup machinesGroup = FabricItemGroup.builder()
				.icon(() -> new ItemStack(Items.STICK))
				.displayName(Text.translatable("itemGroup.thermionics.machines"))
				.build();
		Registry.register(Registries.ITEM_GROUP, itemKey, machinesGroup);
		ItemGroupEvents.modifyEntriesEvent(itemKey).register((group) -> {
			//group.addAll(machineBlocks);
			machineBlocks.forEach(group::add);
		});
	}
	
	private static <E extends BlockEntity> BlockEntityType<E> machineBlock(Identifier id, BlockEntityFactory<E> factory, Block block) {
		Registry.register(Registries.BLOCK, id, block);
		BlockItem item = new BlockItem(block, new Item.Settings());
		Registry.register(Registries.ITEM, id, item);
		machineBlocks.add(new ItemStack(block));
		
		BlockEntityType<E> entityType = BlockEntityType.Builder.create(factory, block).build();
		Registry.register(Registries.BLOCK_ENTITY_TYPE, id, entityType);
		
		return entityType;
	}
	
	private static <E extends BlockEntity, B extends Block & Identified> BlockEntityType<E> machineBlocks(Identifier entityId, BlockEntityFactory<E> factory, B... blocks) {
		for(B block : blocks) {
			Identifier id = block.getid();
			
			Registry.register(Registries.BLOCK, id, block);
			BlockItem item = new BlockItem(block, new Item.Settings());
			Registry.register(Registries.ITEM, id, item);
			machineBlocks.add(new ItemStack(block));
		}
		
		BlockEntityType<E> entityType = BlockEntityType.Builder.create(factory, blocks).build();
		Registry.register(Registries.BLOCK_ENTITY_TYPE, entityId, entityType);
		
		return entityType;
	}
	
	private static void machineWithoutEntity(Identifier id, Block block) {
		Registry.register(Registries.BLOCK, id, block);
		BlockItem item = new BlockItem(block, new Item.Settings());
		Registry.register(Registries.ITEM, id, item);
		machineBlocks.add(new ItemStack(block));
	}
}
