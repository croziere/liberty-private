package com.rdalambic.libertyprivate;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LibertyPrivateBlockListener implements Listener {
	
	private static LibertyPrivate instance;
	
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Material type = block.getType();
		
		if(event.isCancelled()) if(type != Material.WOODEN_DOOR) return;
		
		if(type == Material.WALL_SIGN)
		{
			Sign sign = (Sign) block.getState();
			String text = sign.getLine(0).replaceAll("(?i)\u00A7[0-F]", "").toLowerCase();			
		
			
			// Owner releasing a sign
			if(text.equals("[private]"))
			{
				int length = player.getName().length();
				
				if(sign.getLine(1).replaceAll("(?i)\u00A7[0-F]", "").equals(player.getName().substring(0, length)))
				{
					instance.getLogger().info(player.getName() + " has released a container.");
					return;
				}
				
				if(true) //TODO Check config for admin bypass
				{
					if(player.hasPermission("privateliberty.admin.bypass"))
					{
						instance.getLogger().info(player.getName() + " has released a container owned by " + sign.getLine(1).replaceAll("(?i)\u00A7[0-F]", ""));
					}
				}
				
				event.setCancelled(true);
				sign.update();
			}
			else if(text.equals("[more users]"))
			{
				Block attachedBlock = Util.getSignAttachedBlock(block);
			}
		}
		
	}
	
}
