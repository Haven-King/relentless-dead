package dev.hephaestus.relentlessdead.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;

@Environment(EnvType.CLIENT)
public class DeadClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
	}
}
