package main.feedthecreepertweaks.items;

/*
 * General place to do all your item related recipe things'n'stuff.
 */

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import main.feedthecreepertweaks.ConfigHandler;
import main.feedthecreepertweaks.FeedTheCreeperTweaks;
import main.feedthecreepertweaks.blocks.BlockRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemRecipeRegistry
{
   // Self explanatory. Continue these how you wish. EG: registerSmeltingRecipes
   private static void registerShapedRecipes()
   {
      // GameRegistry.addRecipe(new ShapedOreRecipe(new
      // ItemStack(Items.blaze_rod), new Object[]{"X  ", " X ", "  X", 'X',
      // "powderBlaze"}));  
      
   }

   private static void registerShaplessRecipes()
   {
      Item miniCoal = GameRegistry.findItem("Ztones", "minicoal");
      if(miniCoal != null)
      {
         GameRegistry.addShapelessRecipe(new ItemStack(ItemRegistry.microCoal, 4), new ItemStack(miniCoal, 1));
      }

      Item miniCharcoal = GameRegistry.findItem("Ztones", "minicharcoal");
      if(miniCharcoal != null)
      {
         GameRegistry.addShapelessRecipe(new ItemStack(ItemRegistry.microCoal, 4, 1), new ItemStack(miniCharcoal, 1));
      }
   }

   public static void registerItemRecipes()
   {
      registerShapedRecipes();
      registerShaplessRecipes();
   }
}
