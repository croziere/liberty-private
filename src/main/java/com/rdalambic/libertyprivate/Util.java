package com.rdalambic.libertyprivate;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;

public class Util {
	
	public static Block getSignAttachedBlock(Block b)
	{
		if(b.getType() != Material.WALL_SIGN) return null;
		
		Sign s = (Sign) b.getState().getData();
		BlockFace f = s.getAttachedFace();
		
		if(f != null) return b.getRelative(f);
		
		return null;		
	}
}
