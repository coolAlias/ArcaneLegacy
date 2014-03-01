package arcanelegacy.registry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.Configuration;

import org.lwjgl.input.Keyboard;

import arcanelegacy.handler.ALKeyHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RegisterKeyBindings
{
	/** Key index for easy handling */
	public static final int WAND_GUI = 0, WAND_CHANGE_SLOT = 1;
	
	/** Key descriptions */
	private static final String[] desc = {"Wand Gui","Change Active Slot"};
	
	/** Default key values */
	private static final int[] keyValues = {Keyboard.KEY_G, Keyboard.KEY_H};
	
	/** Maps Keyboard values to AL KeyBinding values */
	public static final Map<Integer, Integer> ALKeyMap = new HashMap<Integer, Integer>();
	
	public static void init(Configuration config)
	{
		KeyBinding[] key = new KeyBinding[desc.length];
		boolean[] repeat = new boolean[desc.length];
		
		for (int i = 0; i < desc.length; ++i)
		{
			key[i] = new KeyBinding(desc[i], config.get(ALKeyHandler.label, desc[i], keyValues[i]).getInt());
			repeat[i] = false;
			ALKeyMap.put(key[i].keyCode, i);
		}
		
        KeyBindingRegistry.registerKeyBinding(new ALKeyHandler(key, repeat));
	}
}