package blue.endless.thermionics.api.transfer.capability;

import blue.endless.thermionics.api.transfer.ShoppingList;
/**
 * 
 */
public interface ShoppingListCapability {
	/**
	 * Extracts items if and only if all items on the ShoppingList can be filled.
	 * @param list
	 * @param simulate
	 * @return
	 */
	public boolean extract(ShoppingList list, boolean simulate);
	/**
	 * Marks fulfilment 
	 * @param list
	 * @param simulate
	 * @return
	 */
	public boolean extractPartial(ShoppingList list, boolean simulate);
}
