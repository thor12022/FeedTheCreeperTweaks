package main.feedthecreepertweaks.items;

/*
 * Class to register your blocks in.
 * The order that you list them here is the order they are registered.
 * Keep that in mind if you like nicely organized creative tabs.
 */

import cpw.mods.fml.common.registry.GameRegistry;
import main.feedthecreepertweaks.ConfigHandler;
import main.feedthecreepertweaks.ModInformation;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ItemRegistry
{
   // items
   public static Item microCoal = new ItemMicroCoal();
   public static Item strongholdWand = new ItemStrongholdWand();
      
   // I use multiple sections here to sort things. It's just my system, you
   // don't have to.
   // Just delete "registerItemSet2" and "registerAllItems" then make this
   // public. Make sure to change the call in the main class.

   public static void registerItems()
   {
      GameRegistry.registerItem(microCoal, ItemMicroCoal.UNLOCALIZED_NAME);
      if(ConfigHandler.strongholdWand)
      {
         GameRegistry.registerItem(strongholdWand, ItemStrongholdWand.UNLOCALIZED_NAME);
      }
   }
}
