package main.feedthecreepertweaks.blocks;

//General place to register all your blocks.

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockRegistry
{

   public static Block blockCobblePopper = new BlockCobblePopper();

   public static void registerBlocks()
   {
      GameRegistry.registerBlock(blockCobblePopper, BlockCobblePopper.UNLOCALIZED_NAME);
   }
}
