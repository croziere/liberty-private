package com.rdalambic.libertyprivate;

import org.bukkit.plugin.java.JavaPlugin;

public final class LibertyPrivate extends JavaPlugin {
	
	//private static boolean enabled = false;
	
	@Override
	public void onEnable()
	{
		if(isEnabled()) return;
		
		super.onEnable();
		
		setEnabled(true);
	}
	
	public void onDisable()
	{
		if(!isEnabled()) return;
		
		super.onDisable();
		
		getLogger().info(" is being disabled.");
		
		setEnabled(false);
	}
}
