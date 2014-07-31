package com.rdalambic.libertyprivate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;

public class Util {

	public static Block getSignAttachedBlock(Block b) {
		if (b.getType() != Material.WALL_SIGN)
			return null;

		Sign s = (Sign) b.getState().getData();
		BlockFace f = s.getAttachedFace();

		if (f != null)
			return b.getRelative(f);

		return null;
	}

	public static Block findBlockOwner(Block b) {
		return findBlockOwner(b, null, false);
	}

	public static Block findBlockOwner(Block b, Block i, boolean iterate) {
		Material m = b.getType();
		Location l;

		if (i != null)
			l = i.getLocation();
		else
			l = null;

		// Check if the block is the privated block

		if (m.equals(Material.CHEST)) // Chest case
		{
			return findBlockOwnerBase(b, l, true, false, false, false, false);
		}

		if ((m.equals(Material.DISPENSER)) || (m.equals(Material.FURNACE))
				|| (m.equals(Material.BURNING_FURNACE))
				|| (m.equals(Material.BREWING_STAND))) { // Cube object case
			return findBlockOwnerBase(b, l, false, false, false, false, false);
		}
		if (m.equals(Material.TRAP_DOOR)) { // Trap case
			return findBlockOwner(getTrapDoorAttachedBlock(b), i, false);
		}
		if ((m.equals(Material.WOODEN_DOOR))
				|| (m.equals(Material.IRON_DOOR_BLOCK))) // Door case
		{
			return findBlockOwnerBase(b, l, true, true, true, true, iterate);
		}

		// Then look for further blocks
		Block c, r;

		c = findBlockOwnerBase(b, l, false, false, false, false, false);
		if (c != null)
			return c;

		// Still no owner, check for weird trap door case
		c = b.getRelative(BlockFace.NORTH);
		if (c.getType().equals(Material.TRAP_DOOR)) {
			if ((c.getData() & 0x3) == 2) {
				c = findBlockOwnerBase(c, l, false, false, false, false, false);
				if (c != null)
					return c;
			}
		}

		c = b.getRelative(BlockFace.EAST);
		if (c.getType().equals(Material.TRAP_DOOR)) {
			if ((c.getData() & 0x3) == 0) {
				c = findBlockOwnerBase(c, l, false, false, false, false, false);
				if (c != null)
					return c;
			}
		}

		c = b.getRelative(BlockFace.SOUTH);
		if (c.getType().equals(Material.TRAP_DOOR)) {
			if ((c.getData() & 0x3) == 3) {
				c = findBlockOwnerBase(c, l, false, false, false, false, false);
				if (c != null)
					return c;
			}
		}

		c = b.getRelative(BlockFace.WEST);
		if (c.getType().equals(Material.TRAP_DOOR)) {
			if ((c.getData() & 0x3) == 1) {
				c = findBlockOwnerBase(c, l, false, false, false, false, false);
				if (c != null)
					return c;
			}
		}

		// Not a trap door case, check for door case
		c = b.getRelative(BlockFace.UP);
		m = c.getType();
		if ((m.equals(Material.WOODEN_DOOR))
				|| (m.equals(Material.IRON_DOOR_BLOCK))) {
			r = findBlockOwnerBase(c, l, true, true, true, true, iterate);
			if (r != null)
				return r;
		}

		c = b.getRelative(BlockFace.DOWN);
		m = c.getType();
		if ((m.equals(Material.WOODEN_DOOR))
				|| (m.equals(Material.IRON_DOOR_BLOCK))) {
			Block c2 = c.getRelative(BlockFace.DOWN);
			m = c2.getType();
			if ((m.equals(Material.WOODEN_DOOR))
					|| m.equals(Material.IRON_DOOR_BLOCK)) {
				return findBlockOwnerBase(c2, l, true, true, false, true,
						iterate);
			} else {
				return findBlockOwnerBase(c, l, true, true, false, true,
						iterate);
			}

		}

		return null;

	}

	private static Block findBlockOwnerBase(Block b, Location l, boolean i,
			boolean up, boolean down, boolean ends, boolean further) {

		Block checkBlock;
		Material m;
		byte face;
		boolean doCheck;

		// Check up and down along door surfaces, with a recursive call and
		// iterate false.

		if (up) {
			checkBlock = b.getRelative(BlockFace.UP);
			m = checkBlock.getType();

			if ((m.equals(Material.WOODEN_DOOR))
					|| (m.equals(Material.IRON_DOOR_BLOCK))) {
				checkBlock = findBlockOwnerBase(checkBlock, l, false, up,
						false, ends, false);
			} else if (ends)
				checkBlock = findBlockOwnerBase(checkBlock, l, false, false,
						false, ends, false);
			else
				checkBlock = null;

			if (checkBlock != null)
				return (checkBlock);
		}

		if (down) {
			checkBlock = b.getRelative(BlockFace.DOWN);
			m = checkBlock.getType();

			if ((m.equals(Material.WOODEN_DOOR))
					|| (m.equals(Material.IRON_DOOR_BLOCK))) {
				checkBlock = findBlockOwnerBase(checkBlock, l, false, false,
						down, ends, false);
			} else if (ends)
				checkBlock = findBlockOwnerBase(checkBlock, l, false, false,
						false, ends, false);
			else
				checkBlock = null;

			if (checkBlock != null)
				return (checkBlock);
		}

		// Check around the originating block, in the order NESW.
		// If a sign is found and it is not the ignored block, check the text.
		// If it is not a sign and iterate is true, do a recursive call with
		// iterate false.
		// (Or further, though this currently backtracks slightly.)

		checkBlock = b.getRelative(BlockFace.NORTH);
		if (checkBlock.getType().equals(Material.WALL_SIGN)) {
			face = checkBlock.getData();
			if (face == 4) {
				// Ignore a sign being created.

				if (l == null)
					doCheck = true;
				else if (checkBlock.getLocation().equals(l))
					doCheck = false;
				else
					doCheck = true;

				if (doCheck) {
					org.bukkit.block.Sign sign = (org.bukkit.block.Sign) checkBlock
							.getState();
					String text = sign.getLine(0)
							.replaceAll("(?i)\u00A7[0-F]", "").toLowerCase();

					if (text.equals("[private]"))
						return (checkBlock);
				}
			}
		} else if (i)
			if (checkBlock.getType().equals(b.getType())) {
				checkBlock = findBlockOwnerBase(checkBlock, l, further, up,
						down, ends, false);
				if (checkBlock != null)
					return (checkBlock);
			}

		checkBlock = b.getRelative(BlockFace.EAST);
		if (checkBlock.getType().equals(Material.WALL_SIGN)) {
			face = checkBlock.getData();
			if (face == 2) {
				// Ignore a sign being created.

				if (l == null)
					doCheck = true;
				else if (checkBlock.getLocation().equals(l))
					doCheck = false;
				else
					doCheck = true;

				if (doCheck) {
					org.bukkit.block.Sign sign = (org.bukkit.block.Sign) checkBlock
							.getState();
					String text = sign.getLine(0)
							.replaceAll("(?i)\u00A7[0-F]", "").toLowerCase();

					if (text.equals("[private]"))
						return (checkBlock);
				}
			}
		} else if (i)
			if (checkBlock.getType().equals(b.getType())) {
				checkBlock = findBlockOwnerBase(checkBlock, l, further, up,
						down, ends, false);
				if (checkBlock != null)
					return (checkBlock);
			}

		checkBlock = b.getRelative(BlockFace.SOUTH);
		if (checkBlock.getType().equals(Material.WALL_SIGN)) {
			face = checkBlock.getData();
			if (face == 5) {
				// Ignore a sign being created.

				if (l == null)
					doCheck = true;
				else if (checkBlock.getLocation().equals(l))
					doCheck = false;
				else
					doCheck = true;

				if (doCheck) {
					org.bukkit.block.Sign sign = (org.bukkit.block.Sign) checkBlock
							.getState();
					String text = sign.getLine(0)
							.replaceAll("(?i)\u00A7[0-F]", "").toLowerCase();

					if (text.equals("[private]"))
						return (checkBlock);
				}
			}
		} else if (i)
			if (checkBlock.getType().equals(b.getType())) {
				checkBlock = findBlockOwnerBase(checkBlock, l, further, up,
						down, ends, false);
				if (checkBlock != null)
					return (checkBlock);
			}

		checkBlock = b.getRelative(BlockFace.WEST);
		if (checkBlock.getType().equals(Material.WALL_SIGN)) {
			face = checkBlock.getData();
			if (face == 3) {
				// Ignore a sign being created.

				if (l == null)
					doCheck = true;
				else if (checkBlock.getLocation().equals(l))
					doCheck = false;
				else
					doCheck = true;

				if (doCheck) {
					org.bukkit.block.Sign sign = (org.bukkit.block.Sign) checkBlock
							.getState();
					String text = sign.getLine(0)
							.replaceAll("(?i)\u00A7[0-F]", "").toLowerCase();

					if (text.equals("[private]"))
						return (checkBlock);
				}
			}
		} else if (i)
			if (checkBlock.getType().equals(b.getType())) {
				checkBlock = findBlockOwnerBase(checkBlock, l, further, up,
						down, ends, false);
				if (checkBlock != null)
					return (checkBlock);
			}

		return (null);

	}
	
	public static Block getTrapDoorAttachedBlock(Block b)
	{
		if(!b.getType().equals(Material.TRAP_DOOR)) return null;
		
		int face = b.getData() & 0x3;
		
		if(face == 3) return(b.getRelative(BlockFace.NORTH));
		if(face == 1) return(b.getRelative(BlockFace.EAST));
		if(face == 2) return(b.getRelative(BlockFace.SOUTH));
		if(face == 0) return(b.getRelative(BlockFace.WEST));
		
		return null;
	}

	public static boolean isProtected(Block block) {
		Material type = block.getType();
		
		if(type.equals(Material.WALL_SIGN))
		{
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
			String text = sign.getLine(0).replaceAll("(?i)\u00A7[0-F]", "").toLowerCase();
			
			if(text.equals("[private]")){
				return true;
			}
			else if(text.equals("[more user]"))
			{
				Block checkBlock = getSignAttachedBlock(block);
				if(checkBlock != null) if(findBlockOwner(checkBlock) != null) {
					return true;
				}
			}
			
		}
		else if(Util.findBlockOwner(block) != null) return true;
		
		return false;
	}
	
	public static BlockFace getPistonFacing(Block block){
		Material type = block.getType();
		
		if((!type.equals(Material.PISTON_BASE)) && (!type.equals(Material.PISTON_STICKY_BASE)) && (!type.equals(Material.PISTON_EXTENSION))){
			return BlockFace.SELF;
		}
		
		int face = block.getData() & 0x7;
		
		switch(face) {
		case 0: return BlockFace.DOWN;
		case 1: return BlockFace.UP;
		case 2: return BlockFace.EAST;
		case 3: return BlockFace.WEST;
		case 4: return BlockFace.NORTH;
		case 5: return BlockFace.SOUTH;
		}
		
		return BlockFace.SELF;
	}	
}
