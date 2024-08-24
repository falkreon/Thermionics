package blue.endless.thermionics.block.entity;

import blue.endless.thermionics.block.BatteryBlock;
import blue.endless.thermionics.block.ThermionicsBlocks;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class BatteryBlockEntity extends BlockEntity {
	private static final int CAPACITY = 8000;
	private static final int MAX_INSERT = 20;
	private static final int MAX_EXTRACT = 20;
	
	private final EnergyStorage energyStorage;
	
	public BatteryBlockEntity(BlockPos pos, BlockState state) {
		super(ThermionicsBlocks.BLOCKENTITY_BATTERY, pos, state);
		
		if (state.getBlock() instanceof BatteryBlock battery && battery.isCreative()) {
			energyStorage = new EnergyStorage() {
				
				@Override
				public long insert(long maxAmount, TransactionContext transaction) {
					return maxAmount;
				}
				
				@Override
				public long extract(long maxAmount, TransactionContext transaction) {
					return maxAmount;
				}
				
				@Override
				public long getAmount() {
					return Long.MAX_VALUE;
				}
				
				@Override
				public long getCapacity() {
					return Long.MAX_VALUE;
				}
				
			};
		} else {
			energyStorage = new SimpleEnergyStorage(CAPACITY, MAX_INSERT, MAX_EXTRACT) {
					@Override
					protected void onFinalCommit() {
						markDirty();
					}
				};
		}
	}
	
	public EnergyStorage getEnergyStorage() {
		return energyStorage;
	}
}
