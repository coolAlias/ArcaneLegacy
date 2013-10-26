package coolalias.arcanelegacy.client.gui;

import org.lwjgl.opengl.GL11;

import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.inventory.ContainerArcaneInscriber;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInscriber;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiArcaneInscriber extends GuiContainer
{
	private static final ResourceLocation iconLocation = new ResourceLocation(ModInfo.ID, "textures/gui/arcaneinscriber.png");
	private TileEntityArcaneInscriber inscriberInventory;

	public GuiArcaneInscriber(InventoryPlayer par1InventoryPlayer, TileEntityArcaneInscriber par2TileEntityArcaneInscriber)
	{
		super(new ContainerArcaneInscriber(par1InventoryPlayer, par2TileEntityArcaneInscriber));
		this.inscriberInventory = par2TileEntityArcaneInscriber;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String s = this.inscriberInventory.isInvNameLocalized() ? this.inscriberInventory.getInvName() : I18n.getString(this.inscriberInventory.getInvName());
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(iconLocation);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		int i1;

		i1 = this.inscriberInventory.getInscribeProgressScaled(24);
		this.drawTexturedModalRect(k + 85, l + 40, 176, 14, i1 + 1, 16);
	}
}
