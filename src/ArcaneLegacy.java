/**
 * @author coolAlias
 *
 */

package coolalias.arcanelegacy;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import coolalias.arcanelegacy.blocks.ALBlocks;
import coolalias.arcanelegacy.client.gui.GuiBuffBar;
import coolalias.arcanelegacy.common.CommonProxy;
import coolalias.arcanelegacy.entity.projectile.EntitySpell;
import coolalias.arcanelegacy.entity.summons.SummonPig;
import coolalias.arcanelegacy.entity.summons.SummonSkeleton;
import coolalias.arcanelegacy.handler.ALEventHandler;
import coolalias.arcanelegacy.item.ALItems;
import coolalias.arcanelegacy.network.ALClientPacketHandler;
import coolalias.arcanelegacy.network.ALServerPacketHandler;
import coolalias.arcanelegacy.registry.RegisterDungeonLoot;
import coolalias.arcanelegacy.registry.RegisterTradeHandler;
import coolalias.arcanelegacy.tabs.TabArcaneBlocks;
import coolalias.arcanelegacy.tabs.TabArcaneRunes;
import coolalias.arcanelegacy.tabs.TabArcanecScrolls;
import coolalias.arcanelegacy.world.gen.WorldGenBlocks;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=false,
clientPacketHandlerSpec = @SidedPacketHandler(channels = {ModInfo.CHANNEL_MAIN, ModInfo.CHANNEL_EXT_PROPS}, packetHandler = ALClientPacketHandler.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = {ModInfo.CHANNEL_MAIN, ModInfo.CHANNEL_EXT_PROPS}, packetHandler = ALServerPacketHandler.class))

public final class ArcaneLegacy
{
	@Instance(ModInfo.ID)
	public static ArcaneLegacy instance = new ArcaneLegacy();

	@SidedProxy(clientSide = ModInfo.CLIENT_PROXY, serverSide = ModInfo.COMMON_PROXY)
	public static CommonProxy proxy;

	// CREATIVE TABS
	public static CreativeTabs tabArcaneBlocks = new TabArcaneBlocks(CreativeTabs.getNextID(), "TabArcaneBlocks");
	public static CreativeTabs tabArcaneRunes = new TabArcaneRunes(CreativeTabs.getNextID(), "TabArcaneRunes");
	public static CreativeTabs tabArcaneScrolls = new TabArcanecScrolls(CreativeTabs.getNextID(), "TabArcaneScrolls");

	// GUI INDEX
	private static int modGuiIndex = 0;
	public static final int mortarPestleGuiId = modGuiIndex++;
	public static final int arcaneInfuserGuiId = modGuiIndex++;
	public static final int arcaneInscriberGuiId = modGuiIndex++;
	public static final int wandGuiId = modGuiIndex++;
	public static final int eyeGuiId = modGuiIndex++;
	public static final int magicBagGuiId = modGuiIndex++;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		System.out.println("[CONFIG] filepath: " + event.getModConfigurationDirectory().getAbsolutePath().toString());
		Config.init(new Configuration(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/ArcaneLegacy.cfg")));
		Config.registerEntities();
		ALBlocks.init();
		ALItems.init();
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		proxy.registerSounds();
		ALBlocks.addRecipes();
		ALItems.addRecipes();
		RegisterDungeonLoot.init();
		if (Config.enableTrading()) RegisterTradeHandler.init();
		GameRegistry.registerWorldGenerator(new WorldGenBlocks());
		MinecraftForge.EVENT_BUS.register(new ALEventHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		if (FMLCommonHandler.instance().getSide().isClient())
			MinecraftForge.EVENT_BUS.register(new GuiBuffBar(Minecraft.getMinecraft()));
	}
}