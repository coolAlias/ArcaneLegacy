package coolalias.arcanelegacy.spells;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import coolalias.arcanelegacy.entity.ExtendedLiving;
import coolalias.arcanelegacy.item.ItemScroll;

/**
 * Currently designed for passive effects, as in those whose effects are handled
 * from outside, such as checking if player can fly.
 * Active effects, such as regeneration, are not yet implemented as SpellEffects
 */
public class SpellEffect
{
	private final ItemScroll.SpellType effectType;
	
	private final int effectID;
	
	private int scrollID, duration;
	
	/**
	 * @param par1Type - spell's effectType
	 * @param par2ID - spell's effectID
	 * @param par3ScrollID - ID of scroll to cast when ready
	 * @param par4Duration - Duration remaining for this effect
	 */
	public SpellEffect(ItemScroll.SpellType type, int effectID, int scrollID, int duration)
	{
		this.effectType = type;
		this.effectID = effectID;
		this.scrollID = scrollID;
		this.duration = duration;
	}

	public final ItemScroll.SpellType getEffectType() {
		return effectType;
	}
	
	public final int getEffectID() {
		return effectID;
	}
	
	public final int getScrollID() {
		return scrollID;
	}
	
	public final int getDuration() {
		return duration;
	}
	
	public final void decrementDuration() {
		--duration;
	}
	
	public final void setDuration(int par1) {
		duration = par1;
	}
	
	public final int getAmplifier() {
		return SpellsMap.instance().getSpell(scrollID).getAmplifier();
	}
	
	/**
	 * Updates spell effect, decrementing duration and performing effect if applicable.
	 * Returns true if spell effect's duration is greater than 0.
	 */
	public boolean onUpdate(EntityLivingBase player)
    {
        if (duration > 0)
        {
            if (SpellUtils.isReady(SpellsMap.instance().getSpell(scrollID), duration))
            {
            	//System.out.println("[EFFECT] Spell is ready. Perform effect.");
            	SpellUtils.performEffect(SpellsMap.instance().getSpell(scrollID), player);
            }

            decrementDuration();
            
            // prevent resurrection spell from expiring while player is resurrecting
            if (effectType == ItemScroll.SpellType.RESURRECT && duration == 0) {
            	if (ExtendedLiving.get(player).getResurrectingTime() > 0)
            		duration = ExtendedLiving.get(player).getResurrectingTime() + 1;
            }
        }

        return duration > 0;
    }
	
	/** Returns true if the two objects are equal */
	public boolean equals(Object par1Obj)
	{
		if (!(par1Obj instanceof SpellEffect))
		{
			return false;
		}
		else
		{
			SpellEffect spelleffect = (SpellEffect) par1Obj;
			return this.effectType == spelleffect.effectType
				&& this.effectID == spelleffect.effectID
				&& this.scrollID == spelleffect.scrollID
				&& this.duration == spelleffect.duration;
		}
	}

	/**
     * merges the input SpellEffect into this one if this.amplifier <= tomerge.amplifier.
     * The duration in the supplied spell effect is assumed to be greater.
     */
    public void combine(SpellEffect spelleffect)
    {
        if (effectType != spelleffect.effectType || effectID != spelleffect.effectID)
        {
            System.err.println("This method should only be called for matching effect types and IDs!");
        }
        
        if (scrollID == spelleffect.scrollID)
        {
        	if (duration < spelleffect.duration) {
                duration = spelleffect.duration;
            }
        }
        else
        {
        	if (SpellsMap.instance().getSpell(spelleffect.scrollID).getAmplifier() > getAmplifier())
            {
                scrollID = spelleffect.scrollID;
                duration = spelleffect.duration;
            }
        	else if (SpellsMap.instance().getSpell(spelleffect.scrollID).getAmplifier() == getAmplifier() && duration < spelleffect.duration)
            {
                duration = spelleffect.duration;
            }
        }
    }
	
	/**
	 * Write a custom spell effect to a player's NBT Tag Compound
	 */
    public NBTTagCompound writeCustomSpellEffectToNBT(NBTTagCompound compound)
    {
        compound.setInteger("Type", effectType.ordinal());
        compound.setInteger("ID", effectID);
        compound.setInteger("Scroll", scrollID);
        compound.setInteger("Duration", duration);
        return compound;
    }

    /**
     * Read a custom spell effect from a player's NBT data
     */
    public static SpellEffect readCustomSpellEffectFromNBT(NBTTagCompound compound)
    {
        int type = compound.getInteger("Type");
        int id = compound.getInteger("ID");
        int scroll = compound.getInteger("Scroll");
        int dur = compound.getInteger("Duration");
        return new SpellEffect(ItemScroll.SpellType.values()[type], id, scroll, dur);
    }
    
    /**
     * Returns a spell effect read from input stream
     */
    public static SpellEffect readFromStream(DataInputStream inputStream)
    {
    	int type, id, scroll, dur;
    	
    	try {
    		type = inputStream.readInt();
			id = inputStream.readInt();
			scroll = inputStream.readInt();
			dur = inputStream.readInt();
    	} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    	
    	return new SpellEffect(ItemScroll.SpellType.values()[type], id, scroll, dur);
    }
    
    /**
     * Writes SpellEffect data to output stream
     */
    public void writeToStream(DataOutputStream outputStream)
    {
    	try {
    		outputStream.writeInt(getEffectType().ordinal());
    		outputStream.writeInt(getEffectID());
    		outputStream.writeInt(getScrollID());
    		outputStream.writeInt(getDuration());
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
}
