package coolalias.arcanelegacy.handler;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.inventory.ContainerWand;
import coolalias.arcanelegacy.item.ItemWand;
import coolalias.arcanelegacy.network.ALServerPacketHandler;
import coolalias.arcanelegacy.registry.RegisterKeyBindings;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ALKeyHandler extends KeyHandler
{
	public static final String label = ModInfo.NAME + " Key";

	private EnumSet tickTypes = EnumSet.of(TickType.PLAYER);

	public ALKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

	@Override
	public EnumSet<TickType> ticks() {
		return tickTypes;
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		if (tickEnd && RegisterKeyBindings.ALKeyMap.containsKey(kb.keyCode))
		{
			EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;

			if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemWand)
			{
				handleWandKeys(player, kb);
			}
		}
	}

	private final void handleWandKeys(EntityPlayer player, KeyBinding kb)
	{
		switch(RegisterKeyBindings.ALKeyMap.get(kb.keyCode)) {
		case RegisterKeyBindings.WAND_GUI:
			if (player.openContainer != null && player.openContainer instanceof ContainerWand) {
				player.closeScreen();
				// this could also be dealt with by sending a packet from the server after receiving the key pressed packet,
				// but why when the client just wants to close the screen?
			} else if (FMLClientHandler.instance().getClient().inGameHasFocus)
				ALServerPacketHandler.sendOpenGuiPacket(ArcaneLegacy.wandGuiId);
			break;
		case RegisterKeyBindings.WAND_CHANGE_SLOT:
			if (player.openContainer != null && player.openContainer instanceof ContainerWand) {
				ALServerPacketHandler.sendNextActiveSlotPacket((byte) 0);
				((ContainerWand) player.openContainer).nextActiveSlot();
			} else if (FMLClientHandler.instance().getClient().inGameHasFocus)
				ALServerPacketHandler.sendNextActiveSlotPacket((byte) 0);
			break;
		}
	}
}
