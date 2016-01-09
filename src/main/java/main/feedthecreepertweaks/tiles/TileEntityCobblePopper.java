package main.feedthecreepertweaks.tiles;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCobblePopper extends TileEntity implements ISidedInventory
{
   private static final int TICK_PRODUCE = 20;
   private static final int TICK_SECONDARY_PRODUCE = 10;
   private static final int TICK_RESET = 20;
   private static final int MAX_STORAGE = 2;
   
   private static final Random random = new Random();
   
   private static final double LAUNCH_VELOCITY_MULTIPLIER = 0.1875d;
   
   private ItemStack cobbleStack = null;
   private int tickCount = 0;
   
   @Override
   public int getSizeInventory()
   {
      return 1 ;
   }
   
   /**
    * 
    * @returns true is there is a block with a solid bottom face above this block
    */
   private boolean isBlockCovered()
   {
      Block block = worldObj.getBlock(xCoord, yCoord + 1, zCoord);
      return block != null && block.isSideSolid(worldObj, xCoord,yCoord + 1, zCoord, ForgeDirection.DOWN);
   }
   
   /**
    * 
    * @returns the number of lava-water pairs around the block
    */
   private int checkForLavaAndWater()
   {
      int lavaCount = 0, waterCount = 0;
      final ForgeDirection[] checkDirections = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST};
      for (ForgeDirection dir : checkDirections)
      {
         Block block = worldObj.getBlock(xCoord + dir.offsetX, yCoord, zCoord+ dir.offsetZ);
         lavaCount += (block == Blocks.lava) ? 1 : 0;
         waterCount += (block == Blocks.water) ? 1 : 0;
     }
      if(lavaCount == 2 && waterCount == 2)
      {
         return 2;
      }
      else if( lavaCount > 0 && waterCount > 0)
      {
         return 1;
      }
      else
      {
         return 0;
      }
   }
   
   private boolean isRedstonePowered()
   {
      /**
       *   @note this functionality was taken from Modular Systems by PaulJoda
       *   https://github.com/TeamCoS/Modular-Systems/blob/28044dbcd1aefc92ebd19f36a606858f9364f0d6/src/main/java/com/pauljoda/modularsystems/core/tiles/DummyRedstoneInput.java#L40
       **/
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
      {
         int weakPower = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ).isProvidingWeakPower(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir.ordinal());
         int strongPower = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ).isProvidingStrongPower(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir.ordinal());
         if (weakPower > 0 || strongPower > 0)
         {
            return true;
         }
     }
      return false;
   }
   
   /**
    * Ejects a stack of a single cobblestone n the air
    * @pre this.cobbleStack must be non-null and valid
    * @post this.cobbleStack stack size is decremented, if stack size 0, this.cobbleStack set to null
    */
   private void launchACobble()
   {
      if(cobbleStack == null)
      {
         return;
      }
      
      ItemStack launchStack = cobbleStack.splitStack(1);
      if(cobbleStack.stackSize == 0)
      {
         cobbleStack = null;
      }
      EntityItem entityItem = new EntityItem(worldObj, xCoord + 0.5f, yCoord + 1.25, zCoord + 0.5f, launchStack);
      entityItem.delayBeforeCanPickup = 5;
      entityItem.motionX = random.nextGaussian() * LAUNCH_VELOCITY_MULTIPLIER;
      entityItem.motionY = LAUNCH_VELOCITY_MULTIPLIER * Math.abs(random.nextGaussian());
      entityItem.motionZ = random.nextGaussian() * LAUNCH_VELOCITY_MULTIPLIER;
      worldObj.spawnEntityInWorld(entityItem);
   }
   
   @Override
   public void updateEntity()
   {
       super.updateEntity();
       
       if(worldObj.isRemote) 
       {
          return;
       }
       
       ++tickCount;
       
       if(tickCount  == TICK_PRODUCE)
       {
          if(isRedstonePowered())
          {
             if(cobbleStack != null && !isBlockCovered())
             {
                launchACobble();
             }
             
             if(cobbleStack == null && checkForLavaAndWater() > 0)
             {
                cobbleStack = new ItemStack(Item.getItemFromBlock(Blocks.cobblestone), 1);
             }
          }
       }
       else if(tickCount == TICK_SECONDARY_PRODUCE)
       {
          if(isRedstonePowered())
          {
             if(cobbleStack != null && !isBlockCovered())
             {
                launchACobble();
             }
             
             if(checkForLavaAndWater() > 1)
             {
                if(cobbleStack == null )
                {
                   cobbleStack = new ItemStack(Item.getItemFromBlock(Blocks.cobblestone), 1);
                }
                else if(cobbleStack.stackSize < MAX_STORAGE)
                {
                   cobbleStack.stackSize++;
                }
             }
          }
       }
       
       if(tickCount == TICK_RESET)
       {
          tickCount = 0;
       }
   }

   @Override
   public ItemStack getStackInSlot(int slot)
   {
      if(slot == 0)
      {
         return cobbleStack;
      }
      else
      {
         return null;
      }
   }

   @Override
   public ItemStack decrStackSize(int slot, int number)
   {
      if(cobbleStack != null && slot == 0 && number > 0)
      {
         ItemStack stack = cobbleStack.splitStack(1);
         if(cobbleStack.stackSize == 0)
         {
            cobbleStack = null;
         }
         return stack;
      }
      else
      {
         return null;
      }
   }

   @Override
   public ItemStack getStackInSlotOnClosing(int p_70304_1_)
   {
      return null;
   }

   @Override
   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
   {
   }

   @Override
   public String getInventoryName()
   {
      return null;
   }

   @Override
   public boolean hasCustomInventoryName()
   {
      return false;
   }

   @Override
   public int getInventoryStackLimit()
   {
      return 0;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
   {
      return false;
   }

   @Override
   public void openInventory()
   {}

   @Override
   public void closeInventory()
   {}

   @Override
   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
   {
      return false;
   }

   @Override
   public int[] getAccessibleSlotsFromSide(int side)
   {
      int[] result = new int[1];
      result[0] = 0;
      return result;
   }

   @Override
   public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_)
   {
      return false;
   }

   @Override
   public boolean canExtractItem(int slot, ItemStack itemStack, int side)
   {
      if(slot == 0)
      {
         return itemStack == cobbleStack;
      }
      return false;
   }

}
