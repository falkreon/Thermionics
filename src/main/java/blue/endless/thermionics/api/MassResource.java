package blue.endless.thermionics.api;

import blue.endless.thermionics.ThermionicsMod;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.component.ComponentChanges;
import net.minecraft.util.Identifier;

/**
 * A MassResource is a resource normally only tracked by quantity, such as heat or energy.
 */
public record MassResource(Identifier id) {
	public static MassResource BLANK = new MassResource(ThermionicsMod.id("empty"));
	public static MassResource HEAT = new MassResource(ThermionicsMod.id("heat"));
	
	
	public static class Variant implements TransferVariant<MassResource> {
		
		private final MassResource resource;
		private final ComponentChanges components;
		
		public Variant(MassResource resource, ComponentChanges components) {
			this.resource = resource;
			this.components = components;
		}
		
		public static Variant blank() {
			return of(BLANK);
		}
		
		@Override
		public boolean isBlank() {
			return resource.equals(BLANK);
		}

		@Override
		public MassResource getObject() {
			return resource;
		}

		@Override
		public ComponentChanges getComponents() {
			return components;
		}
		
		public static Variant of(MassResource resource) {
			return new Variant(resource, ComponentChanges.EMPTY);
		}
		
		public static Variant of(Identifier resourceId) {
			return new Variant(new MassResource(resourceId), ComponentChanges.EMPTY);
		}
	}
}
