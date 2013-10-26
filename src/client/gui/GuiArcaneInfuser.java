package coolalias.arcanelegacy.client.gui;

import org.lwjgl.opengl.GL11;

import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.inventory.ContainerArcaneInfuser;
import coolalias.arcanelegacy.tileentity.TileEntityArcaneInfuser;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiArcaneInfuser extends GuiContainer
{
	private static final ResourceLocation iconLocation = new ResourceLocation(ModInfo.ID, "textures/gui/arcaneinfuser.png");
	private TileEntityArcaneInfuser infuserInventory;

	public GuiArcaneInfuser(InventoryPlayer par1InventoryPlayer, TileEntityArcaneInfuser par2TileEntityArcaneInfuser)
	{
		super(new ContainerArcaneInfuser(par1InventoryPlayer, par2TileEntityArcaneInfuser));
		this.infuserInventory = par2TileEntityArcaneInfuser;
	}

	/** Draw the foreground layer for the GuiContainer (everything in front of the items) */
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		String s = this.infuserInventory.isInvNameLocalized() ? this.infuserInventory.getInvName() : I18n.getString(this.infuserInventory.getInvName());
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	/** Draw the background layer for the GuiContainer (everything behind the items) */
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(iconLocation);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		int i1;

		if (this.infuserInventory.isBurning())
		{
			i1 = this.infuserInventory.getBurnTimeRemainingScaled(12);
			this.drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
		}

		i1 = this.infuserInventory.getCookProgressScaled(24);
		this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
	}
}
