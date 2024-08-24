package blue.endless.thermionics.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class ThermionicsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//BlockRenderLayerMap.INSTANCE.putBlock(null, RenderLayer.getCutoutMipped());
	}
}