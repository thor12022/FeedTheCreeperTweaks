package main.feedthecreepertweaks.handlers;

import com.teammetallurgy.metallurgycm.crafting.RecipesCrusher;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MetallurgyClassicMachinesHandler

{
   private static String configSection = "MetallurgyClassicMachines";
   
   public static void init(FMLInitializationEvent event)
   {
      RecipesCrusher.addOreDicRecipe("stone", new ItemStack(Item.getItemFromBlock(Blocks.cobblestone)), 0.1f);
      RecipesCrusher.addOreDicRecipe("cobblestone", new ItemStack(Item.getItemFromBlock(Blocks.gravel)), 0.1f);
      RecipesCrusher.addOreDicRecipe("gravel", new ItemStack(Item.getItemFromBlock(Blocks.sand)), 0.1f);
      
   }
   
}
