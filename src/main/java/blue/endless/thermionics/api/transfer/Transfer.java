package blue.endless.thermionics.api.transfer;

import java.util.Objects;

import blue.endless.thermionics.ThermionicsMod;
import blue.endless.thermionics.api.MassResource;
import blue.endless.thermionics.api.transfer.capability.StorageCapability;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

public class Transfer<T> {
	public static Transfer<Item> ITEM = new Transfer<Item>(ItemVariant.blank());
	public static Transfer<Fluid> FLUID = new Transfer<Fluid>(FluidVariant.blank());
	public static Transfer<MassResource> MASS_RESOURCE = new Transfer<MassResource>(MassResource.Variant.of(ThermionicsMod.id("empty")));
	
	private final TransferVariant<T> blankVariant;
	private final VariantStack<T> blankStack;
	private final RejectStorage<T> blankStorage;
	
	public Transfer(TransferVariant<T> blankVariant) {
		this.blankVariant = blankVariant;
		this.blankStack = new VariantStack<>(blankVariant, 0);
		this.blankStorage = new RejectStorage<>(blankVariant);
	}
	
	public TransferVariant<T> blankVariant() {
		return blankVariant;
	}
	
	public VariantStack<T> blank() {
		return blankStack;
	}
	
	/**
	 * Returns a StorageCapability for this resource which will report 0 slots and reject all
	 * transfers. Multiple calls to this method will return the same object.
	 * @return a blank StorageCapability
	 */
	public StorageCapability<T> blankStorage() {
		return blankStorage;
	}
	
	public static record Result<T>(VariantStack<T> result, VariantStack<T> rejected) {}
	
	/**
	 * Returns a Result describing what will happen if you try to split count resources out of the
	 * stack. "result" will contain the split-off items, and "rejected" will contain the new state
	 * of the stack after items have been split off from it.
	 * @param stack the stack to split resources out of
	 * @param count the maximum number of resources to split into the result stack
	 * @return a Result containing the outcome of such a split
	 */
	public Result<T> split(VariantStack<T> stack, long count) {
		//No point
		if (stack.isEmpty()) return new Result<>(blankStack, blankStack);
		
		// Reject all?
		if (count <= 0) return new Result<>(blankStack, stack);
		
		// Transfer all?
		if (stack.count() <= count) return new Result<>(stack, blankStack);
		
		// stack size is > count, so transfer Some
		long accepted = count;
		long remainder = stack.count() - accepted;
		
		return new Result<>(stack.copyWithCount(accepted), stack.copyWithCount(remainder));
	}
	
	/**
	 * Attempts to merge source into dest. The returned Result will have the resulting merged stack
	 * in the "result" field, and anything that could not be merged in "rejected".
	 * @param dest   The destination stack that source will be merged into
	 * @param source The stack containing resources to be merged into dest
	 * @return A Result describing how these two VariantStacks should normally merge.
	 */
	public Result<T> merge(VariantStack<T> dest, VariantStack<T> source, long sizeLimit) {
		// incompatible dest, reject all
		if (!Objects.equals(source.variant(), dest.variant())) return new Result<>(dest, source);
		
		// empty dest, accept up to sizeLimit resources
		if (dest.isEmpty()) {
			long mergedSize = Math.min(sizeLimit, source.count());
			long remainderSize = source.count() - mergedSize;
			return (remainderSize <= 0L) ?
					new Result<>(source, blankStack) :
					new Result<>(source.copyWithCount(mergedSize), source.copyWithCount(remainderSize));
		} else {
			long mergedSize = dest.count() + source.count();
			long resultSize = Math.min(mergedSize, sizeLimit);
			long remainderSize = mergedSize - resultSize;
			return (remainderSize <= 0) ?
				new Result<>(dest.copyWithCount(resultSize), blankStack) :
				new Result<>(dest.copyWithCount(mergedSize), dest.copyWithCount(remainderSize));
		}
		
	}
	
	private static class RejectStorage<T> implements StorageCapability<T> {
		private final VariantStack<T> blankStack;
		
		public RejectStorage(TransferVariant<T> blankVariant) {
			this.blankStack = new VariantStack<>(blankVariant, 0L);
		}
		
		/*
		public RejectStorage(VariantStack<T> blankStack) {
			this.blankStack = blankStack;
		}*/
		
		@Override
		public VariantStack<T> insert(int slot, VariantStack<T> stack, boolean simulate) {
			return stack;
		}

		@Override
		public VariantStack<T> insert(VariantStack<T> stack, boolean simulate) {
			return stack;
		}

		@Override
		public VariantStack<T> extract(int slot, long amount, boolean simulate) {
			return blankStack;
		}

		@Override
		public VariantStack<T> extract(TransferVariant<T> variant, long amount, boolean simulate) {
			return blankStack;
		}

		@Override
		public VariantStack<T> extract(T resource, long amount, boolean simulate) {
			return blankStack;
		}

		@Override
		public VariantStack<T> getSlot(int slotNumber) {
			return blankStack;
		}

		@Override
		public int getSlotCount() {
			return 0;
		}
		
	}
}
