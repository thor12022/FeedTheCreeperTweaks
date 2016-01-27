package main.feedthecreepertweaks.handlers;

import com.teammetallurgy.metallurgycm.crafting.RecipesCrusher;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MetallurgyClassicMachinesHandler

{
   private static String configSection = "MetallurgyClassicMachines";
   
   public static void init(FMLInitializationEvent event)
   {
      RecipesCrusher.addOreDicRecipe("stone", new ItemStack(Item.getItemFromBlock(Blocks.cobblestone)), 0f);
      RecipesCrusher.addOreDicRecipe("cobblestone", new ItemStack(Item.getItemFromBlock(Blocks.gravel)), 0f);
      RecipesCrusher.addOreDicRecipe("gravel", new ItemStack(Item.getItemFromBlock(Blocks.sand)), 0f);
      RecipesCrusher.addOreDicRecipe("clothWool", new ItemStack(Items.string,4), 0f);
      RecipesCrusher.addOreDicRecipe("blockGlass", new ItemStack(Item.getItemFromBlock(Blocks.sand)), 0f);
      RecipesCrusher.addRecipe(new ItemStack(Blocks.clay), new ItemStack(Items.clay_ball, 4), 0f);
      RecipesCrusher.addRecipe(new ItemStack(Items.bone), new ItemStack(Items.dye, 6, 15), 0f);
      RecipesCrusher.addOreDicRecipe("oreQuartz", new ItemStack(Items.quartz,4), 0.1f);
      RecipesCrusher.addOreDicRecipe("oreDiamond", new ItemStack(Items.diamond,2), 0.1f);
      RecipesCrusher.addOreDicRecipe("oreCoal", new ItemStack(Items.coal,4), 0.1f);
      RecipesCrusher.addOreDicRecipe("itemBlazeRod", new ItemStack(Items.blaze_powder,4), 0f);
      
   }
   
}
