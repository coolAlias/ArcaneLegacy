package coolalias.arcanelegacy.client.gui;

import org.lwjgl.opengl.GL11;

import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.inventory.ContainerMortarPestle;
import coolalias.arcanelegacy.tileentity.TileEntityMortarPestle;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiMortarPestle extends GuiContainer
{
	private static final ResourceLocation iconLocation = new ResourceLocation(ModInfo.ID, "textures/gui/mortarpestle.png");
	private TileEntityMortarPestle mortarPestleInventory;

	public GuiMortarPestle(InventoryPlayer inventoryPlayer, TileEntityMortarPestle tileEntity)
	{
		super(new ContainerMortarPestle(inventoryPlayer, tileEntity));
		this.mortarPestleInventory = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String containerName = mortarPestleInventory.isInvNameLocalized() ? mortarPestleInventory.getInvName() : StatCollector.translateToLocal(mortarPestleInventory.getInvName());
		this.fontRenderer.drawString(containerName, this.xSize / 2 - this.fontRenderer.getStringWidth(containerName) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(iconLocation);
		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
		int i1;

		/*
		if (this.mortarPestle.isGrinding()) {
			i1 = this.mortarPestle.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(xStart + 46, yStart + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
		}
		 */
		i1 = this.mortarPestleInventory.getGrindProgressScaled(24);
		this.drawTexturedModalRect(xStart + 79, yStart + 34, 176, 14, i1 + 1, 16);
	}
}
