package coolalias.arcanelegacy.client;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import coolalias.arcanelegacy.client.renderer.entity.RenderEntitySpell;
import coolalias.arcanelegacy.client.renderer.entity.RenderArcaneInscriber;
import coolalias.arcanelegacy.common.CommonProxy;
import coolalias.arcanelegacy.entity.projectile.EntitySpell;
//import coolalias.arcanelegacy.entity.projectile.EntitySpellBanish;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInscriber;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntitySpell.class, new RenderEntitySpell(ALItems.runeCharged,0));

		/*
		EntityRegistry.registerModEntity(EntitySpellBanish.class, "Banish Spell", ArcaneLegacy.modEntityIndex++, ArcaneLegacy.instance, 64, 10, true);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellBanish.class, new RenderEntitySpell(ArcaneLegacy.runeCharged,2));

		EntityRegistry.registerModEntity(EntitySpellPotion.class, "Potion Spell", ArcaneLegacy.modEntityIndex++, ArcaneLegacy.instance, 64, 10, true);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpellPotion.class, new RenderEntitySpell(ArcaneLegacy.runeCharged,1));
		 */

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcaneInscriber.class, new RenderArcaneInscriber());
		//TileEntityRenderer.instance.specialRendererMap.put(TileEntityArcaneInscriber.class, new RenderArcaneInscriber());
		//((TileEntitySpecialRenderer) TileEntityRenderer.instance.specialRendererMap.get(TileEntityArcaneInscriber.class)).setTileEntityRenderer(TileEntityRenderer.instance);
	}

	@Override
	public void registerSounds()
	{
		// MinecraftForge.EVENT_BUS.register(new MagicModSounds());
	}
}