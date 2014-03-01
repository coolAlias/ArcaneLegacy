package arcanelegacy.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import arcanelegacy.ArcaneLegacy;
import arcanelegacy.client.gui.GuiArcaneInfuser;
import arcanelegacy.client.gui.GuiArcaneInscriber;
import arcanelegacy.client.gui.GuiMagicBag;
import arcanelegacy.client.gui.GuiMortarPestle;
import arcanelegacy.client.gui.GuiWand;
import arcanelegacy.inventory.ContainerArcaneInfuser;
import arcanelegacy.inventory.ContainerArcaneInscriber;
import arcanelegacy.inventory.ContainerMagicBag;
import arcanelegacy.inventory.ContainerMortarPestle;
import arcanelegacy.inventory.ContainerWand;
import arcanelegacy.inventory.InventoryMagicBag;
import arcanelegacy.inventory.InventoryWand;
import arcanelegacy.tileentity.TileEntityArcaneInfuser;
import arcanelegacy.tileentity.TileEntityArcaneInscriber;
import arcanelegacy.tileentity.TileEntityMortarPestle;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
	public void registerRenderers() {}
	public void registerSounds() {}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == ArcaneLegacy.wandGuiId) {
			return new ContainerWand(player, player.inventory, new InventoryWand(player.getHeldItem()));
		} else if (ID == ArcaneLegacy.magicBagGuiId) {
			return new ContainerMagicBag(player, player.inventory, new InventoryMagicBag(player.getHeldItem()));
		} else {
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
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == ArcaneLegacy.wandGuiId) {
			return new GuiWand(player, player.inventory, new InventoryWand(player.getHeldItem()));
		} else if (ID == ArcaneLegacy.magicBagGuiId) {
			return new GuiMagicBag(player, player.inventory, new InventoryMagicBag(player.getHeldItem()));
		} else {
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
