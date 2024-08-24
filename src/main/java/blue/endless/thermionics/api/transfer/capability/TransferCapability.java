package blue.endless.thermionics.api.transfer.capability;

/**
 * Capability which allows resources to be inserted and extracted.
 * @param <T> The kind of resource (e.g. Item, Fluid)
 */
public interface TransferCapability<T> extends InsertCapability<T>, ExtractCapability<T> {
}
