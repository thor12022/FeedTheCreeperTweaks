package main.feedthecreepertweaks.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import main.feedthecreepertweaks.ModInformation;
import main.feedthecreepertweaks.tiles.TileEntityCobblePopper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockRegistry
{

   public static Block blockCobblePopper = new BlockCobblePopper();

   public static void registerBlocks()
   {
      GameRegistry.registerBlock(blockCobblePopper, BlockCobblePopper.UNLOCALIZED_NAME);
      GameRegistry.registerTileEntity(TileEntityCobblePopper.class, ModInformation.ID + ":" + BlockCobblePopper.UNLOCALIZED_NAME);
   }
}
