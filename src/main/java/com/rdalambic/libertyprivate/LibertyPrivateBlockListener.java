package com.rdalambic.libertyprivate;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
				
				if(attachedBlock == null) return;  
				Block signBlock = Util.findBlockOwner(attachedBlock);
				
				if(signBlock == null) return;
				Sign sign2 = (Sign) signBlock.getState();
				int length = player.getName().length();
				
				if(length > 15) length = 15;
				
				if(sign2.getLine(1).replaceAll("(?i)\u00A7[0-F]", "").equals(player.getName().substring(0, length))) {
					return;
				}
				
				event.setCancelled(true);
				sign.update();				
				
			}
		}
		else
		{
			Block signBlock = Util.findBlockOwner(block);
			
			if(signBlock == null) return;
			
			Sign sign = (Sign) signBlock.getState();
			int length = player.getName().length();
			
			if(length > 15) length = 15;
			
			if(sign.getLine(1).replaceAll("(?i)\u00A7[0-F]", "").equals(player.getName().substring(0, length)))
			{
				instance.getLogger().info(player.getName() + " has released a container.");
			}
			else
			{
				
			}
			return;
		}
		event.setCancelled(true);
		
	}
	
	public void onBlockPistonExtend(BlockPistonExtendEvent event)
	{
		Block block = event.getBlock();
		Block checkBlock;
		
		List<Block> blockList = event.getBlocks();
		
		int x, count = blockList.size();
		
		for(x = 0;x < count; ++x){
			checkBlock = blockList.get(x);
			
			if(Util.isProtected(checkBlock)){
				event.setCancelled(true);
				return;
			}
		}
		
		checkBlock = block.getRelative(Util.getPistonFacing(block), event.getLength() + 1);
		
		if(Util.isProtected(checkBlock))
		{
			event.setCancelled(true);
			return;
		}
	}
	
	public void onBlockPistonRetract(BlockPistonRetractEvent event){
		if(!(event.isSticky())) return;
		
		Block block = event.getBlock();
		Block checkBlock = block.getRelative(Util.getPistonFacing(block), 2);
		Material type = checkBlock.getType();
		
		if(type.equals(Material.CHEST)) return;
		if(type.equals(Material.DISPENSER)) return;
		if(type.equals(Material.FURNACE)) return;
		if(type.equals(Material.BURNING_FURNACE)) return;
		if(type.equals(Material.WOODEN_DOOR)) return;
		if(type.equals(Material.IRON_DOOR_BLOCK)) return;
		//if(type == Material.TRAP_DOOR.getId()) don't return

		if(Util.isProtected(checkBlock)) event.setCancelled(true);
	}
	
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if(event.isCancelled()) return;
		
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();
		Material type = block.getType();
		Block against = event.getBlockAgainst();
		
		if(against.getType().equals(Material.WALL_SIGN)){
			Sign sign = (Sign) against.getState();
			String text = sign.getLine(0).replace("(?i)\u00A7[0-F]", "").toLowerCase();
			
			if(text.equals("[private]") || text.equals("[more users]"))
			{
				event.setCancelled(true);
				return;
			}
		}
		
		//TODO
		//Check for door door blocks
		//Check for misplaced private sign
		//Check for chests expanding
 	}
	
	//Listen Redstone events
	//Listen Sign changes
}
