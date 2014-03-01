package arcanelegacy.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import arcanelegacy.client.gui.GuiBuffBar;
import arcanelegacy.client.renderer.entity.RenderArcaneInscriber;
import arcanelegacy.client.renderer.entity.RenderEntitySpell;
import arcanelegacy.common.CommonProxy;
import arcanelegacy.entity.projectile.EntitySpell;
import arcanelegacy.item.ALItems;
import arcanelegacy.tileentity.TileEntityArcaneInscriber;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
//import coolalias.arcanelegacy.entity.projectile.EntitySpellBanish;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers() {
		MinecraftForge.EVENT_BUS.register(new GuiBuffBar(Minecraft.getMinecraft()));
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