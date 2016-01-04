package main.feedthecreepertweaks.blocks;

import main.feedthecreepertweaks.tiles.TileEntityCobblePopper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCobblePopper extends BlockContainerBase
{
   public static final String UNLOCALIZED_NAME = "cobblePopper";

   public BlockCobblePopper()
   {
      super(UNLOCALIZED_NAME, Material.rock, UNLOCALIZED_NAME, Block.soundTypeStone,  3.5f);
   }

   @Override
   public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
   {
      return new TileEntityCobblePopper();
   }

}
