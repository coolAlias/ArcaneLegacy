package coolalias.arcanelegacy.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import coolalias.arcanelegacy.ModInfo;
import coolalias.arcanelegacy.inventory.InventoryWand;
import coolalias.arcanelegacy.spells.SpellUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWand extends Item
{
	// should be stored in NBT for SMP compatibility
	/** Set each time the wand starts charging */
	private int wandCastTime = 0;

	@SideOnly(Side.CLIENT)
	private Icon[] iconArray;

	String iconName = null;

	public ItemWand(int par1) {
		super(par1);
		this.maxStackSize = 1;
		this.setMaxDamage(256);
	}

	/** Returns number of ticks required for wand to cast currently equipped spell */
	public int getWandCastTime() { return wandCastTime; }

	/** Sets the number of ticks required for the wand to cast currently equipped spell */
	public void setWandCastTime(int par1) { wandCastTime = par1; }

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) { return 72000; }

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) { return EnumAction.bow; }

	@Override
	public ItemStack onItemRightClick(ItemStack wand, World world, EntityPlayer player)
	{
		ItemStack scroll = new InventoryWand(wand).getStackInSlot(InventoryWand.ACTIVE_SLOT);
		if (scroll == null) {
			System.out.println("[WAND] No spell to cast!");
		} else {
			((ItemWand) wand.getItem()).setWandCastTime(((ItemScroll) scroll.getItem()).getCastTime() / 2);
			System.out.println("[WAND] Wand cast time: " + ((ItemWand) wand.getItem()).getWandCastTime());
			ArrowNockEvent event = new ArrowNockEvent(player, wand);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled()) { return event.result; }
			player.setItemInUse(wand, this.getMaxItemUseDuration(wand));
		}

		return wand;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack wand, World world, EntityPlayer player, int par4)
	{
		System.out.println("[WAND] on stopped using; world is " + world.isRemote);
		int ticksInUse = this.getMaxItemUseDuration(wand) - par4;
		ArrowLooseEvent event = new ArrowLooseEvent(player, wand, ticksInUse);
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled())
		{
			System.out.println("[WAND] Spell must have been interrupted. Damn.");
			// still need to damage wand when interrupted and used for enough time
			if (ticksInUse > ((ItemWand) wand.getItem()).getWandCastTime())
				damageWand(wand, world, player);
			return;
		}
		ticksInUse = event.charge;
		ItemStack scroll = new InventoryWand(wand).getStackInSlot(InventoryWand.ACTIVE_SLOT);

		if (scroll == null)
		{
			System.out.println("[WAND] No spell to cast!");
			return;
		}

		if (ticksInUse < ((ItemWand) wand.getItem()).getWandCastTime())
		{
			System.out.println("[WAND] Your spell fizzles...");
			return;
		}
		else
		{
			if (world.rand.nextFloat() < SpellUtils.getSuccessChance(player))
				// if (!player.worldObj.isRemote)
			{
				System.out.println("[WAND] Spell cast successfully!");
				if (player.capabilities.isCreativeMode || player.inventory.hasItem(scroll.itemID));
				{
					if (((ItemScroll) scroll.getItem()).getRange() == ItemScroll.Range.MISSILE) {
						SpellUtils.defaultRangedSpell(scroll, world, player);
					} else {
						SpellUtils.defaultSpell(scroll, world, player, null, false);
					}
				}
			} else {
				// play spell failure sound
				System.out.println("[WAND] Armor interfered - spell failed!");
			}

			damageWand(wand, world, player);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering() { return true; }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon(ModInfo.ID + ":" + getIconName().toLowerCase() + "_0");
		iconArray = new Icon[4];

		for (int i = 0; i < iconArray.length; ++i) {
			iconArray[i] = iconRegister.registerIcon(ModInfo.ID + ":" + iconName.toLowerCase() + "_" + i);
		}
	}

	private String getIconName()
	{
		this.iconName = this.getUnlocalizedName().substring(5);

		if (iconName.contains("wand_Wood")) {
			iconName = "wand_wood";
		} else if (iconName.contains("wand_Bone")) {
			iconName = "wand_bone";
		} else if (iconName.contains("wand_Blaze")) {
			iconName = "wand_blaze";
		} else {}

		return iconName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
	{
		if (usingItem == null) return this.itemIcon;

		int ticksInUse = stack.getMaxItemUseDuration() - useRemaining;
		int index = this.wandCastTime - ticksInUse;

		if (ticksInUse > this.wandCastTime) {
			return this.iconArray[3];
		} else if (ticksInUse > 0 && (index % 4 == 2 || index % 4 == 3)) {
			return this.iconArray[2];
		} else if (ticksInUse > 0  && (index % 4 == 0 || index % 4 == 1)) {
			return this.iconArray[1];
		} else {
			return this.iconArray[0];
		}
	}

	/**
	 * Applies damage to wand, checks if broken and releases inventory items when broken
	 */
	private final void damageWand(ItemStack wand, World world, EntityPlayer player)
	{
		InventoryWand invWand = new InventoryWand(wand);

		// Damage wand even if spell failed due to armor interference
		wand.damageItem(((ItemWand) wand.getItem()).getWandCastTime(), player);

		// this is done for bows in Item method 'damageItem'
		if (wand.stackSize == 0 && wand.getItem() instanceof ItemWand)
		{
			if (!world.isRemote) {
				// drop scrolls inside
				for (int i = 0; i < invWand.getSizeInventory(); ++i) {
					if (invWand.getStackInSlot(i) != null)
						world.spawnEntityInWorld(new EntityItem(world, player.posX + world.rand.nextFloat() * 0.8F + 0.1F, player.posY + world.rand.nextFloat() * 0.8F + 0.1F, player.posZ + world.rand.nextFloat() * 0.8F + 0.1F, invWand.getStackInSlot(i)));
				}
			}
			// destroy wand
			player.destroyCurrentEquippedItem();
		}
	}
}