package coolalias.arcanelegacy.client.gui;

import java.util.Collection;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.inventory.InventoryWand;
import coolalias.arcanelegacy.item.ItemScroll;
import coolalias.arcanelegacy.item.ItemWand;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

@SideOnly(Side.CLIENT)
public class GuiBuffBar extends Gui
{
	private Minecraft mc;
	private static final ResourceLocation iconLocation = new ResourceLocation(ModInfo.ID, "textures/gui/containers/potionicons.png");

	// for POTIONS
	private static final int BUFF_ICON_SIZE = 18;
	private static final int BUFF_ICON_SPACING = BUFF_ICON_SIZE + 2; // 2 pixels between buff icons
	private static final int BUFF_ICON_BASE_U_OFFSET = 0;
	private static final int BUFF_ICON_BASE_V_OFFSET = 0; // orig 198 for inventory.png
	private static final int BUFF_ICONS_PER_ROW = 8;

	// for SCROLLS
	private static final int SCROLL_ICON_SIZE = 28;
	private static final int SCROLL_ICONS_PER_ROW = 9;
	private ItemStack lastHeld;
	private InventoryWand wand;

	public GuiBuffBar(Minecraft mc)
	{
		super();
		// We need this to invoke the render engine.
		this.mc = mc;
	}

	//
	// This event is called by GuiIngameForge during each frame by
	// GuiIngameForge.pre() and GuiIngameForce.post().
	//
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent event)
	{
		//
		// We draw after the ExperienceBar has drawn.  The event raised by GuiIngameForge.pre()
		// will return true from isCancelable.  If you call event.setCanceled(true) in
		// that case, the portion of rendering which this event represents will be canceled.
		// We want to draw *after* the experience bar is drawn, so we make sure isCancelable() returns
		// false and that the eventType represents the ExperienceBar event.
		if (event.isCancelable() || event.type != ElementType.EXPERIENCE)
		{
			return;
		}

		// Starting position for the buff bar - 2 pixels from the top left corner.
		int xPos = 2;
		int yPos = 2;
		Collection collection = this.mc.thePlayer.getActivePotionEffects();
		if (!collection.isEmpty())
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			this.mc.getTextureManager().bindTexture(iconLocation);

			for (Iterator iterator = this.mc.thePlayer.getActivePotionEffects()
					.iterator(); iterator.hasNext(); xPos += BUFF_ICON_SPACING)
			{
				PotionEffect potioneffect = (PotionEffect) iterator.next();
				Potion potion = Potion.potionTypes[potioneffect.getPotionID()];

				if (potion.hasStatusIcon())
				{
					int iconIndex = potion.getStatusIconIndex();
					this.drawTexturedModalRect(
							xPos, yPos,
							BUFF_ICON_BASE_U_OFFSET + iconIndex % BUFF_ICONS_PER_ROW * BUFF_ICON_SIZE,
							BUFF_ICON_BASE_V_OFFSET + iconIndex / BUFF_ICONS_PER_ROW * BUFF_ICON_SIZE,
							BUFF_ICON_SIZE, BUFF_ICON_SIZE);
				}
				// Draws box corners around edge of icon
				// this.mc.func_110434_K().func_110577_a(iconLocation);
				//this.drawTexturedModalRect(xPos, yPos, 24 % BUFF_ICONS_PER_ROW * BUFF_ICON_SIZE,
				//24 / BUFF_ICONS_PER_ROW * BUFF_ICON_SIZE, BUFF_ICON_SIZE, BUFF_ICON_SIZE);
			}
		}

		// Draw active wand spell here
		// ExtendedPlayerProperties props = (ExtendedPlayerProperties) this.mc.thePlayer.getExtendedProperties(ExtendedPlayerProperties.EXT_PROP_NAME);
		// if (props == null) { System.out.println("[GUI BUFF] Extended Player Properties is null."); }
		if (this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemWand)
		{
			if (this.lastHeld != this.mc.thePlayer.getHeldItem())
			{
				wand = new InventoryWand(this.mc.thePlayer.getHeldItem());
				this.lastHeld = this.mc.thePlayer.getHeldItem();
			}
			xPos = 2;
			yPos = 26;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);

			// Draws spell scroll icon, if any
			if (wand != null && wand.getStackInSlot(InventoryWand.ACTIVE_SLOT) != null)
			{
				ResourceLocation spellIcon = new ResourceLocation(ModInfo.ID, "textures/gui/containers/spellicons.png");
				int iconIndex = ((ItemScroll) wand.getStackInSlot(InventoryWand.ACTIVE_SLOT).getItem()).getStatusIconIndex();
				//System.out.println("[GUI] icon index: " + iconIndex + ", index % iconsperrow: " + iconIndex % SCROLL_ICONS_PER_ROW + 
				//		", index/iconsperrow: " + iconIndex / SCROLL_ICONS_PER_ROW);
				this.mc.getTextureManager().bindTexture(spellIcon);
				this.drawTexturedModalRect(xPos, yPos, iconIndex % SCROLL_ICONS_PER_ROW * SCROLL_ICON_SIZE,
						iconIndex / SCROLL_ICONS_PER_ROW * SCROLL_ICON_SIZE, SCROLL_ICON_SIZE, SCROLL_ICON_SIZE);
			}
		}
	}
}
