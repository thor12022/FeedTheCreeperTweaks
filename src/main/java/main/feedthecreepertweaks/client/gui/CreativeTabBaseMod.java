package main.feedthecreepertweaks.client.gui;

//Creates your creative tab.

import main.feedthecreepertweaks.ModInformation;
import main.feedthecreepertweaks.items.ItemRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemStack;

public class CreativeTabBaseMod extends CreativeTabs
{

   public CreativeTabBaseMod(String tabLabel)
   {
      super(tabLabel);
      setBackgroundImageName(ModInformation.ID + ".png"); // Automagically has
                                                          // tab_ applied to it.
                                                          // Make sure you
                                                          // change the texture
                                                          // name.
   }

   public boolean hasSearchBar()
   {
      return true;
   }

   // The tab icon is what you return here.
   @Override
   public ItemStack getIconItemStack()
   {
      return new ItemStack(new ItemPiston(Blocks.sticky_piston));
   }

   @Override
   public Item getTabIconItem()
   {
      return new Item();
   }
}
