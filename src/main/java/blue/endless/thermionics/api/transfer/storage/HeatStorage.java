package blue.endless.thermionics.api.transfer.storage;

import blue.endless.thermionics.api.MassResource;

public class HeatStorage extends MassResourceStorage {
	public HeatStorage(long limit) {
		super(MassResource.HEAT, limit);
	}
}
