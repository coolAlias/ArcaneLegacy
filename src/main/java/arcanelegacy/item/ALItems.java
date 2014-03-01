package arcanelegacy.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import arcanelegacy.ArcaneLegacy;
import arcanelegacy.Config;
import arcanelegacy.blocks.ALBlocks;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ALItems
{
	// POTION EFFECT SCROLLS
	public static Item scrollHarmI, scrollHarmII, scrollHarmMassI, scrollHarmMassII, 
	scrollHarmRangedI, scrollHarmRangedII, scrollHarmRangedMassI, scrollHarmRangedMassII,
	scrollHarmAuraI;
	public static Item scrollHealI, scrollHealII, scrollHealMassI, scrollHealMassII,
	scrollHealRangedI, scrollHealRangedII, scrollHealRangedMassI, scrollHealRangedMassII,
	scrollHealAuraI;
	public static Item scrollSpeedI, scrollSpeedII;
	public static Item scrollTestMultiEffects;

	// BANISH SCROLLS
	public static Item scrollBanish, scrollBanishMass;

	// CHARM SCROLLS
	public static Item scrollCharm, scrollCharmMass;

	// CREATE BLOCK SCROLLS
	public static Item scrollDarknessI, scrollDarknessII;
	public static Item scrollLightI, scrollLightII;
	public static Item scrollWeb, scrollWebRanged;
	public static Item scrollImprisonI, scrollImprisonII, scrollImprisonIII;

	// EARTH SCROLLS
	public static Item scrollGrowthI, scrollGrowthII, scrollGrowthIII;
	public static Item scrollShockwaveI, scrollShockwaveII;
	public static Item scrollWiltI, scrollWiltII;

	// FIRE SCROLLS
	public static Item scrollCombust, scrollCombustArea, scrollFireArrow, scrollFireball, scrollFireAura;

	// ICE SCROLLS
	public static Item scrollFreezeI, scrollFreezeII, scrollFreezeBoltI, scrollFreezeBoltII, scrollFreezeAuraI;

	// PROTECTION SCROLLS
	public static Item scrollProtectMagicI;

	// SUMMON SCROLLS
	public static Item scrollSummonPig;
	public static Item scrollSummonSkeletonI, scrollSummonSkeletonII, scrollSummonSkeletonIII;

	// WIND SCROLLS
	public static Item scrollGustI, scrollGustII, scrollGustIII;
	public static Item scrollRepulsionField;

	// SCROLLS, MISC
	public static Item scrollDispel;
	public static Item scrollEgress;
	public static Item scrollFeatherFall;
	public static Item scrollFly, scrollFlyExtended;
	public static Item scrollJumpI, scrollJumpII;
	public static Item scrollResurrectI, scrollResurrectII;
	public static Item scrollSpiderClimb;
	public static Item scrollTeleportI, scrollTeleportII, scrollTeleportIII, scrollBlink;
	public static Item scrollNullifyTeleportI, scrollNullificationField;

	// WANDS
	public static Item wandBasic;

	// MISCELLANEOUS ITEMS
	public static Item magicBag;
	public static Item scrollBlank;
	public static Item runeBasic;
	public static Item runeCharged;
	public static Item arcaneChisel;
	public static Item spiritShard;
	public static Item dust;

	public static final void init()
	{
		// AURA SCROLLS - put optional scrolls first so status icon index can be adjusted in ItemScroll
		if (Config.enableAuraSpells()) {
			scrollFireAura = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.FIRE_SPELL,0,20,5,true).addScrollEffect(Potion.fireResistance.id, 20, 0, 1.0F, true).setIsAura(true).setChanceScaled(false).setRange(ItemScroll.Range.SELF).setEffectRadius(2.0D).setUnlocalizedName("scroll_Fire_Aura");
			scrollFreezeAuraI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.ICE_SPELL,Block.ice.blockID,30,10,true).setRange(ItemScroll.Range.SELF).setEffectRadius(2.0D).setIsAura(true).addScrollEffect(Potion.moveSlowdown.id, 10, 0, 1.0F, true).setDamage(0.5D,false).setChanceScaled(false).setUnlocalizedName("scroll_Freeze_Aura_I");
			scrollGrowthIII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GROWTH,0,10,10,true).setRange(ItemScroll.Range.SELF).setEffectRadius(2.0D).setIsAura(true).setUnlocalizedName("scroll_Growth_III");
			scrollHarmAuraI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,45).setScrollEffect(Potion.harm.id,20,0,1.0F,true).setRange(ItemScroll.Range.SELF).setEffectRadius(3.0D).setIsAura(true).setChanceScaled(false).setDamageScaled(false).setUnlocalizedName("scroll_Harm_Aura_I");
			scrollHealAuraI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,25).setScrollEffect(Potion.regeneration.id,20,0,1.0F,true).setRange(ItemScroll.Range.SELF).setEffectRadius(3.0D).setIsAura(true).setChanceScaled(false).setDamageScaled(false).setUnlocalizedName("scroll_Heal_Aura_I");
			scrollRepulsionField = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GENERIC,0,30,20,true).setRange(ItemScroll.Range.SELF).setEffectRadius(4.0D).setIsAura(true).setKnockback(1).setUnlocalizedName("scroll_Repulsion_Field");
			scrollWiltII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.WILT,0,30,10,true).setRange(ItemScroll.Range.SELF).setEffectRadius(1.0D).setIsAura(true).setChanceScaled(false).setUnlocalizedName("scroll_Wilt_II");
			scrollNullificationField = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.TELEPORT,ItemScroll.NULLIFY,60,30,true).setRange(ItemScroll.Range.SELF).setEffectRadius(5.0D).setIsAura(true).setChanceScaled(false).setUnlocalizedName("scroll_Nullification_Field");
			// one more icon slot to fill for auras
		}
		// IMPRISON SCROLLS
		if (Config.enableImprisonSpells()) {
			scrollImprisonI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,Block.dirt.blockID,0,25,false).setRange(ItemScroll.Range.MISSILE).setEffectRadius(2.0D).setUnlocalizedName("scroll_Imprison_I");
			scrollImprisonII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,Block.stone.blockID,0,40,false).setRange(ItemScroll.Range.MISSILE).setEffectRadius(2.0D).setUnlocalizedName("scroll_Imprison_II");
			scrollImprisonIII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,Block.stone.blockID,0,60,false).setRange(ItemScroll.Range.MISSILE).setEffectRadius(2.0D).setUnlocalizedName("scroll_Imprison_III");
		}
		// TICKING BLOCK SCROLLS
		if (Config.enableTickingBlocks()) {
			scrollDarknessI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,ALBlocks.blockDarkness.blockID,2,10,false).setEffectRadius(2.0D).setUnlocalizedName("scroll_Darkness_I");
			scrollLightI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,ALBlocks.blockLight.blockID,30,10,true).setIsAura(true).setUnlocalizedName("scroll_Light_I");
		}
		// POTION EFFECT SCROLLS
		scrollHarmI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,5).setScrollEffect(Potion.harm.id,1,0,1.0F,false).setRange(ItemScroll.Range.TOUCH).setDamageScaled(false).setUnlocalizedName("scroll_Harm_I");
		scrollHarmII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,10).setScrollEffect(Potion.harm.id,1,1,1.0F,false).setRange(ItemScroll.Range.TOUCH).setDamageScaled(false).setUnlocalizedName("scroll_Harm_II");
		scrollHarmMassI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,15).setScrollEffect(Potion.harm.id,1,0,1.0F,false).setRange(ItemScroll.Range.TOUCH).setEffectRadius(3.0D).setChanceScaled(false).setDamageScaled(false).setUnlocalizedName("scroll_Harm_Mass_I");
		scrollHarmMassII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,20).setScrollEffect(Potion.harm.id,1,1,1.0F,false).setRange(ItemScroll.Range.TOUCH).setEffectRadius(3.0D).setChanceScaled(false).setDamageScaled(false).setUnlocalizedName("scroll_Harm_Mass_II");
		scrollHarmRangedI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,10).setScrollEffect(Potion.harm.id,1,0,1.0F,false).setRange(ItemScroll.Range.MISSILE).setDamageScaled(false).setUnlocalizedName("scroll_Harm_Ranged_I");
		scrollHarmRangedII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,15).setScrollEffect(Potion.harm.id,1,1,1.0F,false).setRange(ItemScroll.Range.MISSILE).setDamageScaled(false).setUnlocalizedName("scroll_Harm_Ranged_II");
		scrollHarmRangedMassI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,25).setScrollEffect(Potion.harm.id,1,0,1.0F,false).setEffectRadius(3.0D).setRange(ItemScroll.Range.MISSILE).setChanceScaled(false).setDamageScaled(false).setUnlocalizedName("scroll_Harm_Ranged_Mass_I");
		scrollHarmRangedMassII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,30).setScrollEffect(Potion.harm.id,1,1,1.0F,false).setEffectRadius(3.0D).setRange(ItemScroll.Range.MISSILE).setChanceScaled(false).setDamageScaled(false).setUnlocalizedName("scroll_Harm_Ranged_Mass_II");

		scrollHealI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,5).setScrollEffect(Potion.heal.id,1,0,1.0F,false).setDamageScaled(false).setUnlocalizedName("scroll_Heal_I");
		scrollHealII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,10).setScrollEffect(Potion.heal.id,1,1,1.0F,false).setDamageScaled(false).setUnlocalizedName("scroll_Heal_II");
		scrollHealMassI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,10).setScrollEffect(Potion.heal.id,1,0,1.0F,false).setEffectRadius(3.0D).setChanceScaled(false).setDamageScaled(false).setUnlocalizedName("scroll_Heal_Mass_I");
		scrollHealMassII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,15).setScrollEffect(Potion.heal.id,1,1,1.0F,false).setEffectRadius(3.0D).setChanceScaled(false).setDamageScaled(false).setUnlocalizedName("scroll_Heal_Mass_II");
		scrollHealRangedI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,10).setScrollEffect(Potion.heal.id,1,0,1.0F,false).setRange(ItemScroll.Range.MISSILE).setDamageScaled(false).setUnlocalizedName("scroll_Heal_Ranged_I");
		scrollHealRangedII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,15).setScrollEffect(Potion.heal.id,1,1,1.0F,false).setRange(ItemScroll.Range.MISSILE).setDamageScaled(false).setUnlocalizedName("scroll_Heal_Ranged_II");
		scrollHealRangedMassI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,20).setScrollEffect(Potion.heal.id,1,0,1.0F,false).setEffectRadius(3.0D).setChanceScaled(false).setDamageScaled(false).setRange(ItemScroll.Range.MISSILE).setUnlocalizedName("scroll_Heal_Ranged_Mass_I");
		scrollHealRangedMassII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,25).setScrollEffect(Potion.heal.id,1,1,1.0F,false).setEffectRadius(3.0D).setChanceScaled(false).setDamageScaled(false).setRange(ItemScroll.Range.MISSILE).setUnlocalizedName("scroll_Heal_Ranged_Mass_II");

		scrollSpeedI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,15).setScrollEffect(Potion.moveSpeed.id,60,0,1.0F,true).setRange(ItemScroll.Range.SELF).setUnlocalizedName("scroll_Boost_I");
		scrollSpeedII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,20).setScrollEffect(Potion.moveSpeed.id,60,1,1.0F,true).setRange(ItemScroll.Range.SELF).setEffectRadius(3.0).setUnlocalizedName("scroll_Boost_II");

		scrollTestMultiEffects = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.POTION,15).setScrollEffect(Potion.moveSpeed.id,60,0,1.0F,true).addScrollEffect(Potion.invisibility.id).addScrollEffect(Potion.regeneration.id).setUnlocalizedName("scroll_multi_test");

		// BANISH SCROLLS
		scrollBanish = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.BANISH,25).setRange(ItemScroll.Range.MISSILE).setChance(0, 0.75F).setUnlocalizedName("scroll_Banish");
		scrollBanishMass = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.BANISH,25).setRange(ItemScroll.Range.MISSILE).setEffectRadius(5.0D).setChance(0, 0.5F).setChanceScaled(false).setUnlocalizedName("scroll_Banish_Mass");

		// CHARM SCROLLS
		scrollCharm = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CHARM,25).setChance(0, 0.5F).setRange(ItemScroll.Range.MISSILE).setUnlocalizedName("scroll_Charm_I");
		scrollCharmMass = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CHARM,25).setChance(0, 0.5F).setRange(ItemScroll.Range.MISSILE).setEffectRadius(3.0D).setChanceScaled(false).setUnlocalizedName("scroll_Charm_II");

		// CREATE BLOCK SCROLLS
		scrollDarknessII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,ALBlocks.blockDarkness.blockID,0,50,true).setEffectRadius(3.0D).setUnlocalizedName("scroll_Darkness_II");
		scrollLightII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,ALBlocks.blockLight.blockID,0,30,true).setUnlocalizedName("scroll_Light_II");

		scrollWeb = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,(Config.enableTickingBlocks() ? ALBlocks.blockWebTicking.blockID : Block.web.blockID),(Config.enableTickingBlocks() ? 2 : 0),10,false).setEffectRadius(2.0D).setUnlocalizedName("scroll_Web");
		scrollWebRanged = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.CREATE_BLOCK,(Config.enableTickingBlocks() ? ALBlocks.blockWebTicking.blockID : Block.web.blockID),(Config.enableTickingBlocks() ? 2 : 0),15,false).setRange(ItemScroll.Range.MISSILE).setEffectRadius(2.0D).setUnlocalizedName("scroll_Web_Ranged");

		// EARTH SCROLLS
		scrollGrowthI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GROWTH,0,3,10,false).setUnlocalizedName("scroll_Growth_I");
		scrollGrowthII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GROWTH,0,3,10,false).setEffectRadius(2.0D).setUnlocalizedName("scroll_Growth_II");

		scrollShockwaveI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GENERIC,15).setEffectRadius(3.0D).setDamage(2.0D,false).setKnockback(2).setChanceScaled(false).setUnlocalizedName("scroll_Shockwave_I");
		scrollShockwaveII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GENERIC,30).setEffectRadius(4.0D).setDamage(4.0D,false).setKnockback(3).setChanceScaled(false).setUnlocalizedName("scroll_Shockwave_II");

		scrollWiltI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.WILT,10).setEffectRadius(1.0D).setChanceScaled(false).setUnlocalizedName("scroll_Wilt_I");

		// FIRE SCROLLS
		scrollCombust = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.FIRE_SPELL,0,8,5,false).setChanceScaled(false).setRange(ItemScroll.Range.TOUCH).setUnlocalizedName("scroll_Combust");
		scrollCombustArea = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.FIRE_SPELL,0,8,5,false).setChanceScaled(false).setEffectRadius(3.0D).setRange(ItemScroll.Range.TOUCH).setUnlocalizedName("scroll_Combust_Area");
		scrollFireArrow = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.FIRE_SPELL,0,8,5,false).setChanceScaled(false).setEffectRadius(0.25D).setRange(ItemScroll.Range.MISSILE).setDamage(5.0D,false).setUnlocalizedName("scroll_Fire_Arrow");
		scrollFireball = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.FIRE_SPELL,0,8,5,false).setChanceScaled(false).setEffectRadius(5.0D).setRange(ItemScroll.Range.MISSILE).setDamage(10.0D,true).setKnockback(2).setUnlocalizedName("scroll_Fireball");

		// ICE SCROLLS
		scrollFreezeI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.ICE_SPELL,Block.ice.blockID,0,10,false).setRange(ItemScroll.Range.TOUCH).setEffectRadius(2.0D).addScrollEffect(Potion.moveSlowdown.id, 10, 0, 1.0F, true).setDamage(4.0D,false).setChanceScaled(false).setUnlocalizedName("scroll_Freeze_I");
		scrollFreezeII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.ICE_SPELL,Block.ice.blockID,0,20,false).setRange(ItemScroll.Range.TOUCH).setEffectRadius(3.0D).addScrollEffect(Potion.moveSlowdown.id, 10, 1, 1.0F, true).setDamage(7.0D,false).setChanceScaled(false).setUnlocalizedName("scroll_Freeze_II");
		scrollFreezeBoltI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.ICE_SPELL,Block.ice.blockID,0,25,false).setRange(ItemScroll.Range.MISSILE).setEffectRadius(1.0D).addScrollEffect(Potion.moveSlowdown.id, 10, 0, 1.0F, true).setDamage(4.0D,false).setChanceScaled(false).setUnlocalizedName("scroll_Freeze_Bolt_I");
		scrollFreezeBoltII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.ICE_SPELL,Block.ice.blockID,0,40,false).setRange(ItemScroll.Range.MISSILE).setEffectRadius(2.0D).addScrollEffect(Potion.moveSlowdown.id, 10, 1, 1.0F, true).setDamage(7.0D,false).setChanceScaled(false).setUnlocalizedName("scroll_Freeze_Bolt_II");

		// PROTECTION SCROLLS
		scrollProtectMagicI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.PROTECTION,ItemScroll.PROTECT_MAGIC,30,20,true).setAmplifier(2).setRange(ItemScroll.Range.SELF).setUnlocalizedName("scroll_protect_magic_i");

		// SUMMON SCROLLS
		scrollSummonPig = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.SUMMON,ItemScroll.PIG,(Config.enablePermanentSummons() ? -1 : Config.baseSummonDuration()),20,true).setEffectRadius(2.0D).setUnlocalizedName("scroll_Summon_Pig");
		scrollSummonSkeletonI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.SUMMON,ItemScroll.SKELETON,(Config.enablePermanentSummons() ? -1 : Config.baseSummonDuration()*3),40,true).setEffectRadius(2.0D).setUnlocalizedName("scroll_Summon_Skeleton_I");
		scrollSummonSkeletonII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.SUMMON,ItemScroll.SKELETON,(Config.enablePermanentSummons() ? -1 : Config.baseSummonDuration()*2),40,true).addScrollEffect(ItemScroll.SKELETON).setEffectRadius(2.5D).setUnlocalizedName("scroll_Summon_Skeleton_II");
		scrollSummonSkeletonIII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.SUMMON,ItemScroll.SKELETON,(Config.enablePermanentSummons() ? -1 : Config.baseSummonDuration()),40,true).addScrollEffect(ItemScroll.SKELETON).addScrollEffect(ItemScroll.SKELETON).setEffectRadius(3.0D).setUnlocalizedName("scroll_Summon_Skeleton_III");

		// WIND SCROLLS
		scrollGustI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GENERIC,0,0,4,false).setRange(ItemScroll.Range.MISSILE).setKnockback(3).setUnlocalizedName("scroll_Gust_I");
		scrollGustII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GENERIC,0,0,8,false).setRange(ItemScroll.Range.MISSILE).setKnockback(4).setUnlocalizedName("scroll_Gust_II");
		scrollGustIII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.GENERIC,0,0,12,false).setRange(ItemScroll.Range.MISSILE).setKnockback(5).setUnlocalizedName("scroll_Gust_III");

		// MISCELLANEOUS SCROLLS
		scrollDispel = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.DISPEL,20).setEffectRadius(3.0D).setChance(0, 0.80F).setChanceScaled(false).setUnlocalizedName("scroll_Dispel");

		scrollEgress = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.EGRESS,20).setRange(ItemScroll.Range.SELF).setUnlocalizedName("scroll_Egress");

		scrollFeatherFall = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.FEATHER_FALL,0,10,10,true).setRange(ItemScroll.Range.SELF).setUnlocalizedName("scroll_Feather_Fall");
		scrollFly = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.FLY_SPELL,0,10,30,true).setRange(ItemScroll.Range.SELF).setUnlocalizedName("scroll_Fly");
		scrollFlyExtended = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.FLY_SPELL,0,120,40,true).setRange(ItemScroll.Range.SELF).setUnlocalizedName("scroll_Fly_Ext");
		scrollJumpI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.JUMP_SPELL,0,30,10,true).setRange(ItemScroll.Range.SELF).setAmplifier(0).setUnlocalizedName("scroll_Jump_I");
		scrollJumpII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.JUMP_SPELL,0,10,10,true).setRange(ItemScroll.Range.SELF).setAmplifier(1).setUnlocalizedName("scroll_Jump_II");

		scrollResurrectI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.RESURRECT,0,60,20,true).setRange(ItemScroll.Range.SELF).setAmplifier(0).setUnlocalizedName("scroll_Resurrect_I");
		scrollResurrectII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.RESURRECT,0,60,20,true).setRange(ItemScroll.Range.SELF).setAmplifier(1).setUnlocalizedName("scroll_Resurrect_II");

		scrollSpiderClimb = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.SPIDER_CLIMB,0,60,20,true).setRange(ItemScroll.Range.SELF).setUnlocalizedName("scroll_Spider_Climb");

		scrollTeleportI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.TELEPORT,10).setRange(ItemScroll.Range.SELF).setAmplifier(8).setUnlocalizedName("scroll_Teleport_I");
		scrollTeleportII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.TELEPORT,20).setRange(ItemScroll.Range.SELF).setAmplifier(24).setUnlocalizedName("scroll_Teleport_II");
		scrollTeleportIII = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.TELEPORT,ItemScroll.PHASE,1,15,false).setRange(ItemScroll.Range.SELF).setAmplifier(8).setUnlocalizedName("scroll_Teleport_III");
		scrollBlink = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.TELEPORT,ItemScroll.BLINK,30,10,true).setRange(ItemScroll.Range.SELF).setUnlocalizedName("scroll_Blink");

		scrollNullifyTeleportI = new ItemScroll(Config.nextModItemID(),ItemScroll.SpellType.TELEPORT,ItemScroll.NULLIFY,30,15,true).setRange(ItemScroll.Range.SELF).setChanceScaled(false).setUnlocalizedName("scroll_Nullify_I");

		// WANDS
		wandBasic = new ItemWand(Config.nextModItemID()).setUnlocalizedName("wand_Bone").setCreativeTab(ArcaneLegacy.tabArcaneRunes).setFull3D();

		// MISCELLANEOUS ITEMS
		scrollBlank = new ItemScrollBase(Config.nextModItemID()).setUnlocalizedName("scroll_Blank");
		runeBasic = new ItemRune(Config.nextModItemID()).setUnlocalizedName("rune_Basic");
		runeCharged = new ItemRune(Config.nextModItemID()).setCharge(true).setUnlocalizedName("rune_Charged");
		if (Config.enableRuneCopying()) arcaneChisel = new BaseModItem(Config.nextModItemID()).setUnlocalizedName("arcane_Chisel").setCreativeTab(ArcaneLegacy.tabArcaneRunes);
		spiritShard = new BaseModItem(Config.nextModItemID()).setUnlocalizedName("spirit_Shard").setCreativeTab(ArcaneLegacy.tabArcaneRunes);
		dust = new ItemDust(Config.nextModItemID()).setUnlocalizedName("dust_Base");
		if (Config.enableMagicBag()) magicBag = new ItemMagicBag(Config.nextModItemID()).setUnlocalizedName("magic_Bag").setCreativeTab(CreativeTabs.tabMisc);

		addNames();
	}

	private static final void addNames()
	{
		// SCROLLS
		if (Config.enableAuraSpells())
		{
			LanguageRegistry.addName(ALItems.scrollFireAura, EnumChatFormatting.LIGHT_PURPLE + "Fire Aura");
			LanguageRegistry.addName(ALItems.scrollFreezeAuraI, EnumChatFormatting.LIGHT_PURPLE + "Freezing Aura I");
			LanguageRegistry.addName(ALItems.scrollGrowthIII, EnumChatFormatting.LIGHT_PURPLE + "Nature's Aura");
			LanguageRegistry.addName(ALItems.scrollHarmAuraI, EnumChatFormatting.LIGHT_PURPLE + "Aura of Pain I");
			LanguageRegistry.addName(ALItems.scrollHealAuraI, EnumChatFormatting.LIGHT_PURPLE + "Aura of Healing I");
			LanguageRegistry.addName(ALItems.scrollRepulsionField, EnumChatFormatting.LIGHT_PURPLE + "Repulsion Field");
			LanguageRegistry.addName(ALItems.scrollWiltII, EnumChatFormatting.LIGHT_PURPLE + "Wilting Aura");
			LanguageRegistry.addName(ALItems.scrollNullificationField, EnumChatFormatting.LIGHT_PURPLE + "Nullification Field");
		}
		if (Config.enableImprisonSpells())
		{
			LanguageRegistry.addName(ALItems.scrollImprisonI, EnumChatFormatting.LIGHT_PURPLE + "Earthen Prison");
			LanguageRegistry.addName(ALItems.scrollImprisonII, EnumChatFormatting.LIGHT_PURPLE + "Stone Prison");
			LanguageRegistry.addName(ALItems.scrollImprisonIII, EnumChatFormatting.LIGHT_PURPLE + "Crushing Prison");
		}
		if (Config.enableTickingBlocks()) {
			LanguageRegistry.addName(ALItems.scrollDarknessI, EnumChatFormatting.LIGHT_PURPLE + "Darkness");
			LanguageRegistry.addName(ALItems.scrollLightI, EnumChatFormatting.LIGHT_PURPLE + "Light");
		}
		LanguageRegistry.addName(ALItems.scrollBanish, EnumChatFormatting.LIGHT_PURPLE + "Banish");
		LanguageRegistry.addName(ALItems.scrollBanishMass, EnumChatFormatting.LIGHT_PURPLE + "Mass Banish");
		LanguageRegistry.addName(ALItems.scrollBlink, EnumChatFormatting.LIGHT_PURPLE + "Blink");
		LanguageRegistry.addName(ALItems.scrollCharm, EnumChatFormatting.LIGHT_PURPLE + "Charm");
		LanguageRegistry.addName(ALItems.scrollCharmMass, EnumChatFormatting.LIGHT_PURPLE + "Mass Charm");
		LanguageRegistry.addName(ALItems.scrollCombust, EnumChatFormatting.LIGHT_PURPLE + "Combust");
		LanguageRegistry.addName(ALItems.scrollCombustArea, EnumChatFormatting.LIGHT_PURPLE + "Mass Combustion");
		LanguageRegistry.addName(ALItems.scrollDarknessII, EnumChatFormatting.LIGHT_PURPLE + "Lasting Darkness");
		LanguageRegistry.addName(ALItems.scrollDispel, EnumChatFormatting.LIGHT_PURPLE + "Dispel");
		LanguageRegistry.addName(ALItems.scrollEgress, EnumChatFormatting.LIGHT_PURPLE + "Egress");
		LanguageRegistry.addName(ALItems.scrollFeatherFall, EnumChatFormatting.LIGHT_PURPLE + "Feather Fall");
		LanguageRegistry.addName(ALItems.scrollFireball, EnumChatFormatting.LIGHT_PURPLE + "Fireball");
		LanguageRegistry.addName(ALItems.scrollFireArrow, EnumChatFormatting.LIGHT_PURPLE + "Flame Arrow");
		LanguageRegistry.addName(ALItems.scrollFly, EnumChatFormatting.LIGHT_PURPLE + "Fly");
		LanguageRegistry.addName(ALItems.scrollFlyExtended, EnumChatFormatting.LIGHT_PURPLE + "Fly (Extended)");
		LanguageRegistry.addName(ALItems.scrollFreezeI, EnumChatFormatting.LIGHT_PURPLE + "Freeze I");
		LanguageRegistry.addName(ALItems.scrollFreezeII, EnumChatFormatting.LIGHT_PURPLE + "Freeze II");
		LanguageRegistry.addName(ALItems.scrollFreezeBoltI, EnumChatFormatting.LIGHT_PURPLE + "Freeze Bolt I");
		LanguageRegistry.addName(ALItems.scrollFreezeBoltII, EnumChatFormatting.LIGHT_PURPLE + "Freeze Bolt II");
		LanguageRegistry.addName(ALItems.scrollGrowthI, EnumChatFormatting.LIGHT_PURPLE + "Nature's Touch I");
		LanguageRegistry.addName(ALItems.scrollGrowthII, EnumChatFormatting.LIGHT_PURPLE + "Nature's Touch II");
		LanguageRegistry.addName(ALItems.scrollGustI, EnumChatFormatting.LIGHT_PURPLE + "Gust I");
		LanguageRegistry.addName(ALItems.scrollGustII, EnumChatFormatting.LIGHT_PURPLE + "Gust II");
		LanguageRegistry.addName(ALItems.scrollGustIII, EnumChatFormatting.LIGHT_PURPLE + "Gust III");
		LanguageRegistry.addName(ALItems.scrollHarmI, EnumChatFormatting.LIGHT_PURPLE + "Harm I");
		LanguageRegistry.addName(ALItems.scrollHarmII, EnumChatFormatting.LIGHT_PURPLE + "Harm II");
		LanguageRegistry.addName(ALItems.scrollHarmMassI, EnumChatFormatting.LIGHT_PURPLE + "Mass Harm I");
		LanguageRegistry.addName(ALItems.scrollHarmMassII, EnumChatFormatting.LIGHT_PURPLE + "Mass Harm II");
		LanguageRegistry.addName(ALItems.scrollHarmRangedI, EnumChatFormatting.LIGHT_PURPLE + "Ranged Harm I");
		LanguageRegistry.addName(ALItems.scrollHarmRangedII, EnumChatFormatting.LIGHT_PURPLE + "Ranged Harm II");
		LanguageRegistry.addName(ALItems.scrollHarmRangedMassI, EnumChatFormatting.LIGHT_PURPLE + "Mass Ranged Harm I");
		LanguageRegistry.addName(ALItems.scrollHarmRangedMassII, EnumChatFormatting.LIGHT_PURPLE + "Mass Ranged Harm II");
		LanguageRegistry.addName(ALItems.scrollHealI, EnumChatFormatting.LIGHT_PURPLE + "Heal I");
		LanguageRegistry.addName(ALItems.scrollHealII, EnumChatFormatting.LIGHT_PURPLE + "Heal II");
		LanguageRegistry.addName(ALItems.scrollHealMassI, EnumChatFormatting.LIGHT_PURPLE + "Mass Heal I");
		LanguageRegistry.addName(ALItems.scrollHealMassII, EnumChatFormatting.LIGHT_PURPLE + "Mass Heal II");
		LanguageRegistry.addName(ALItems.scrollHealRangedI, EnumChatFormatting.LIGHT_PURPLE + "Ranged Heal I");
		LanguageRegistry.addName(ALItems.scrollHealRangedII, EnumChatFormatting.LIGHT_PURPLE + "Ranged Heal II");
		LanguageRegistry.addName(ALItems.scrollHealRangedMassI, EnumChatFormatting.LIGHT_PURPLE + "Mass Ranged Heal I");
		LanguageRegistry.addName(ALItems.scrollHealRangedMassII, EnumChatFormatting.LIGHT_PURPLE + "Mass Ranged Heal II");
		LanguageRegistry.addName(ALItems.scrollJumpI, EnumChatFormatting.LIGHT_PURPLE + "Jump I");
		LanguageRegistry.addName(ALItems.scrollJumpII, EnumChatFormatting.LIGHT_PURPLE + "Jump II");
		LanguageRegistry.addName(ALItems.scrollLightII, EnumChatFormatting.LIGHT_PURPLE + "Lasting Light");
		LanguageRegistry.addName(ALItems.scrollNullifyTeleportI, EnumChatFormatting.LIGHT_PURPLE + "Nullify");
		LanguageRegistry.addName(ALItems.scrollProtectMagicI, EnumChatFormatting.LIGHT_PURPLE + "Magic Ward I");
		LanguageRegistry.addName(ALItems.scrollResurrectI, EnumChatFormatting.LIGHT_PURPLE + "Resurrect I");
		LanguageRegistry.addName(ALItems.scrollResurrectII, EnumChatFormatting.LIGHT_PURPLE + "Resurrect II");
		LanguageRegistry.addName(ALItems.scrollShockwaveI, EnumChatFormatting.LIGHT_PURPLE + "Shockwave I");
		LanguageRegistry.addName(ALItems.scrollShockwaveII, EnumChatFormatting.LIGHT_PURPLE + "Shockwave II");
		LanguageRegistry.addName(ALItems.scrollSpeedI, EnumChatFormatting.LIGHT_PURPLE + "Boost I");
		LanguageRegistry.addName(ALItems.scrollSpeedII, EnumChatFormatting.LIGHT_PURPLE + "Boost II");
		LanguageRegistry.addName(ALItems.scrollSpiderClimb, EnumChatFormatting.LIGHT_PURPLE + "Spider Climb");
		LanguageRegistry.addName(ALItems.scrollSummonPig, EnumChatFormatting.LIGHT_PURPLE + "Summon Pig");
		LanguageRegistry.addName(ALItems.scrollSummonSkeletonI, EnumChatFormatting.LIGHT_PURPLE + "Summon Skeleton I");
		LanguageRegistry.addName(ALItems.scrollSummonSkeletonII, EnumChatFormatting.LIGHT_PURPLE + "Summon Skeleton II");
		LanguageRegistry.addName(ALItems.scrollSummonSkeletonIII, EnumChatFormatting.LIGHT_PURPLE + "Summon Skeleton III");
		LanguageRegistry.addName(ALItems.scrollTeleportI, EnumChatFormatting.LIGHT_PURPLE + "Ethereal Stride");
		LanguageRegistry.addName(ALItems.scrollTeleportII, EnumChatFormatting.LIGHT_PURPLE + "Teleport");
		LanguageRegistry.addName(ALItems.scrollTeleportIII, EnumChatFormatting.LIGHT_PURPLE + "Phase Door");
		LanguageRegistry.addName(ALItems.scrollTestMultiEffects, EnumChatFormatting.LIGHT_PURPLE + "Miracle");
		LanguageRegistry.addName(ALItems.scrollWeb, EnumChatFormatting.LIGHT_PURPLE + "Web");
		LanguageRegistry.addName(ALItems.scrollWebRanged, EnumChatFormatting.LIGHT_PURPLE + "Ensnare");
		LanguageRegistry.addName(ALItems.scrollWiltI, EnumChatFormatting.LIGHT_PURPLE + "Wilt");

		// DUSTS
		for (int i = 0; i < ItemDust.DUST_NAMES.length; i++) {
			ItemStack dust = new ItemStack(ALItems.dust,1,i);
			if (i == ItemDust.DUST_SPIRIT) {
				LanguageRegistry.addName(dust, "Magic Essence");
			} else {
				LanguageRegistry.addName(dust, ItemDust.DUST_NAMES[i] + " Dust");
			}
		}

		// RUNES
		for (int i = 0; i < ItemRune.RUNE_NAME.length; i++) {
			ItemStack rune1 = new ItemStack(ALItems.runeBasic,1,i);
			ItemStack rune2 = new ItemStack(ALItems.runeCharged,1,i);
			LanguageRegistry.addName(rune1, ItemRune.RUNE_NAME[i] + " Rune");
			LanguageRegistry.addName(rune2, "Charged " + ItemRune.RUNE_NAME[i] + " Rune");
		}

		// WANDS
		LanguageRegistry.addName(ALItems.wandBasic, "Generic Wand");

		// MISCELLANEOUS ITEMS
		LanguageRegistry.addName(ALItems.scrollBlank, "Blank Scroll");
		LanguageRegistry.addName(ALItems.spiritShard, "Spirit Shard");
		if (Config.enableRuneCopying()) LanguageRegistry.addName(ALItems.arcaneChisel, "Rune Chisel");
		if (Config.enableMagicBag()) LanguageRegistry.addName(ALItems.magicBag, "Bag of Holding");
	}

	public static final void addRecipes()
	{
		// Shaped Recipes

		// Shapeless Recipes
		GameRegistry.addShapelessRecipe(new ItemStack(ALItems.scrollFireArrow,4), Item.stick, Item.stick, Item.stick, Item.stick);
		
		// RUNE RECIPES
		if (Config.enableRuneCopying())
		{
			GameRegistry.addRecipe(new ItemStack(ALItems.arcaneChisel), "x", "y", 'x', Item.diamond, 'y', Block.obsidian);
			for (int i = 0; i < ItemRune.RUNE_NAME.length; i++) {
				GameRegistry.addShapelessRecipe(new ItemStack(ALItems.runeBasic,2,i), new ItemStack(ALItems.runeBasic,1,i), new ItemStack(ALItems.runeBasic,1,0), ALItems.arcaneChisel);
				// GameRegistry.addShapelessRecipe(new ItemStack(ArcaneLegacy.runeCharged,1,i), new ItemStack(ArcaneLegacy.runeBasic,1,i));
			}
		}

		// MISCELLANEOUS ITEM RECIPES
		if (Config.enableMagicBagRecipe()) {
			GameRegistry.addRecipe(new ItemStack(ALItems.magicBag), new Object[] {"XZX","XYX","XXX", 'X', Item.leather.itemID, 'Y', Item.eyeOfEnder.itemID, 'Z', Item.silk.itemID});
		}
	}
}
