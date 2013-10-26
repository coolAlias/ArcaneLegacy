package coolalias.arcanelegacy.client;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import coolalias.arcanelegacy.ModInfo;

public class ALSounds {
	@ForgeSubscribe
	// @SideOnly(Side.CLIENT)
	public void onSound(SoundLoadEvent event)
	{
		String [] soundFiles = {
		};
		for (int i = 0; i < soundFiles.length; i++)
		{
			System.out.println("[MAGIC MOD] Loading sound " + ModInfo.ID + ":" + soundFiles[i]);
			event.manager.addSound(ModInfo.ID + ":" + soundFiles[i]);
		}
	}
}
