package blue.endless.thermionics.api.transfer.capability;

/**
 * Combined storage capability which enables the full suite of interactions with a container:
 * insert, extract, and read/inspect contents
 * @param <T> the type of resource (e.g. Fluid, Item)
 */
public interface StorageCapability<T> extends TransferCapability<T>, InspectStorageCapability<T> {
}
