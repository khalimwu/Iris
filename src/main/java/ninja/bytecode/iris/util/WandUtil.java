package ninja.bytecode.iris.util;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import ninja.bytecode.iris.schematic.Schematic;
import ninja.bytecode.shuriken.collections.GList;

public class WandUtil
{
	@SuppressWarnings("deprecation")
	public static void pasteSchematic(Schematic s, Location at)
	{
		Location start = at.clone().add(s.getOffset());

		for(BlockVector i : s.getSchematic().keySet())
		{
			MB b = s.getSchematic().get(i);
			System.out.println("Pasted " + b.material + " @ " + start.clone().add(i));
			start.clone().add(i).getBlock().setTypeIdAndData(b.material.getId(), b.data, false);
		}
	}

	@SuppressWarnings("deprecation")
	public static Schematic createSchematic(ItemStack wand, Location at)
	{
		if(!isWand(wand))
		{
			return null;
		}

		try
		{
			Location[] f = getCuboid(wand);
			Cuboid c = new Cuboid(f[0], f[1]);
			Vector v = at.clone().subtract(c.getLowerNE()).toVector();
			Schematic s = new Schematic(c.getSizeX(), c.getSizeY(), c.getSizeZ(), v.getBlockX(), v.getBlockY(), v.getBlockZ());
			Iterator<Block> bb = c.iterator();
			while(bb.hasNext())
			{
				Block b = bb.next();

				if(b.getType().equals(Material.AIR))
				{
					continue;
				}
				
				BlockVector bv = b.getLocation().toVector().toBlockVector().subtract(c.getLowerNE().toVector().toBlockVector()).toBlockVector();
				System.out.println("Load " + bv + " " + b.getType());
				s.put(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ(), new MB(b.getType(), b.getData()));
			}

			return s;
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static Location stringToLocation(String s)
	{
		try
		{
			String[] f = s.split("\\Q in \\E");
			String[] g = f[0].split("\\Q,\\E");
			return new Location(Bukkit.getWorld(f[1]), Integer.valueOf(g[0]), Integer.valueOf(g[1]), Integer.valueOf(g[2]));
		}

		catch(Throwable e)
		{
			return null;
		}
	}

	public static String locationToString(Location s)
	{
		if(s == null)
		{
			return "<#>";
		}

		return s.getBlockX() + "," + s.getBlockY() + "," + s.getBlockZ() + " in " + s.getWorld().getName();
	}

	public static ItemStack createWand()
	{
		return createWand(null, null);
	}

	public static ItemStack update(boolean left, Location a, ItemStack item)
	{
		if(!isWand(item))
		{
			return item;
		}

		Location[] f = getCuboid(item);
		Location other = left ? f[1] : f[0];

		return createWand(left ? a : other, left ? other : a);
	}

	public static ItemStack createWand(Location a, Location b)
	{
		ItemStack is = new ItemStack(Material.BLAZE_ROD);
		is.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.BOLD + "" + ChatColor.GOLD + "Wand of Iris");
		im.setUnbreakable(true);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS);
		im.setLore(new GList<String>().add(locationToString(a), locationToString(b)));
		is.setItemMeta(im);

		return is;
	}

	public static Location[] getCuboid(ItemStack is)
	{
		ItemMeta im = is.getItemMeta();
		return new Location[] {stringToLocation(im.getLore().get(0)), stringToLocation(im.getLore().get(1))};
	}

	public static boolean isWand(ItemStack item)
	{
		if(!item.getType().equals(createWand().getType()))
		{
			return false;
		}

		if(!item.getItemMeta().getEnchants().equals(createWand().getItemMeta().getEnchants()))
		{
			return false;
		}

		if(!item.getItemMeta().getDisplayName().equals(createWand().getItemMeta().getDisplayName()))
		{
			return false;
		}

		return true;
	}
}
