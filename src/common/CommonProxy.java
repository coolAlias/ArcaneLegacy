package coolalias.arcanelegacy.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import coolalias.arcanelegacy.ArcaneLegacy;
import coolalias.arcanelegacy.client.gui.GuiArcaneInfuser;
import coolalias.arcanelegacy.client.gui.GuiArcaneInscriber;
import coolalias.arcanelegacy.client.gui.GuiMagicBag;
import coolalias.arcanelegacy.client.gui.GuiMortarPestle;
import coolalias.arcanelegacy.client.gui.GuiWand;
import coolalias.arcanelegacy.entity.ExtendedPlayer;
import coolalias.arcanelegacy.inventory.ContainerArcaneInfuser;
import coolalias.arcanelegacy.inventory.ContainerArcaneInscriber;
import coolalias.arcanelegacy.inventory.ContainerMagicBag;
import coolalias.arcanelegacy.inventory.ContainerMortarPestle;
import coolalias.arcanelegacy.inventory.ContainerWand;
import coolalias.arcanelegacy.inventory.InventoryMagicBag;
import coolalias.arcanelegacy.inventory.InventoryWand;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInfuser;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInscriber;
import coolalias.arcanelegacy.tileentity.TileEntityMortarPestle;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
	public void registerRenderers() {}
	public void registerSounds() {}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		ExtendedPlayer props = ExtendedPlayer.get(player);

		if (ID == ArcaneLegacy.wandGuiId)
		{
			return new ContainerWand(player, player.inventory, new InventoryWand(player.getHeldItem()));
		}
		else if (ID == ArcaneLegacy.magicBagGuiId)
		{
			return new ContainerMagicBag(player, player.inventory, new InventoryMagicBag(player.getHeldItem()));
		}
		else
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityMortarPestle){
				return new ContainerMortarPestle(player.inventory, (TileEntityMortarPestle) tileEntity);
			} else if (tileEntity instanceof TileEntityArcaneInfuser){
				return new ContainerArcaneInfuser(player.inventory, (TileEntityArcaneInfuser) tileEntity);
			} else if (tileEntity instanceof TileEntityArcaneInscriber){
				return new ContainerArcaneInscriber(player.inventory, (TileEntityArcaneInscriber) tileEntity);
			} else {
				return null;
			}
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		ExtendedPlayer props = ExtendedPlayer.get(player);

		if (ID == ArcaneLegacy.wandGuiId)
		{
			return new GuiWand(player, player.inventory, new InventoryWand(player.getHeldItem()));
		}
		else if (ID == ArcaneLegacy.magicBagGuiId)
		{
			return new GuiMagicBag(player, player.inventory, new InventoryMagicBag(player.getHeldItem()));
		}
		else
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityMortarPestle){
				return new GuiMortarPestle(player.inventory, (TileEntityMortarPestle) tileEntity);
			} else if (tileEntity instanceof TileEntityArcaneInfuser) {
				return new GuiArcaneInfuser(player.inventory, (TileEntityArcaneInfuser) tileEntity);
			} else if (tileEntity instanceof TileEntityArcaneInscriber) {
				return new GuiArcaneInscriber(player.inventory, (TileEntityArcaneInscriber) tileEntity);
			}
		}

		return null;
	}
}
