package blue.endless.thermionics.block.entity;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import blue.endless.thermionics.api.MassResource;
import blue.endless.thermionics.api.transfer.capability.ShoppingListCapability;
import blue.endless.thermionics.api.transfer.capability.StorageCapability;
import blue.endless.thermionics.api.transfer.capability.TransferCapabilityProvider;
import blue.endless.thermionics.api.transfer.storage.MassResourceStorage;
import blue.endless.thermionics.api.transfer.storage.TransferStorage;
import blue.endless.thermionics.block.ThermionicsBlocks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;

public class ThermalPipeBlockEntity extends BlockEntity implements TransferCapabilityProvider {
	
	private TransferStorage<Fluid> fluidContents = TransferStorage.fluidTank(FluidConstants.INGOT);
	private MassResourceStorage heatContents = new MassResourceStorage(MassResource.HEAT, 8L);
	private Vector3dc fluidFlow = new Vector3d(0,0,0);
	private Vector3dc heatFlow = new Vector3d(0,0,0);
	
	public ThermalPipeBlockEntity(BlockPos pos, BlockState state) {
		super(ThermionicsBlocks.BLOCKENTITY_THERMAL_PIPE, pos, state);
	}
	
	@Override
	protected void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
		// TODO Auto-generated method stub
		super.readNbt(nbt, registryLookup);
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
		// TODO Auto-generated method stub
		
		super.writeNbt(nbt, registryLookup);
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public <T> StorageCapability<T> getStorageCapability(Class<T> resourceType) {
		if (resourceType == Fluid.class) {
			return (StorageCapability<T>) fluidContents;
		} else if (resourceType == MassResource.class) {
			return (StorageCapability<T>) heatContents;
		}
		
		return null;
	}

	@Override
	public <T> ShoppingListCapability getShoppingListCapability() {
		// TODO Auto-generated method stub
		return null;
	}
}
