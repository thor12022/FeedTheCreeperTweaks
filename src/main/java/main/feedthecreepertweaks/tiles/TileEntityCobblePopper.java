package main.feedthecreepertweaks.tiles;

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
   private static final int TICK_REDSTONE_CHECK = 18;
   private static final int TICK_X_CHECK = 10;
   private static final int TICK_Y_CHECK = 15;
   private static final int TICK_Z_CHECK = 5;
   
   private static final float LAUNCH_Y_VELOCITY = 0.5f;
   
   private ItemStack cobbleStack = null;
   private int tickCount = 0;
   private int lavaCount = 0;
   private int waterCount = 0;
   private boolean isCovered = false;
   private boolean isPowered = false;
   
   @Override
   public int getSizeInventory()
   {
      return 1 ;
   }
   
   @Override
   public void updateEntity()
   {
       super.updateEntity();
       
       if (worldObj.isRemote) 
       {
          return;
       }
       
       ++tickCount;
       
       final int[] posDeltas = {-1, 1};
       
       if(tickCount  == TICK_PRODUCE)
       {
          if(cobbleStack != null && !isCovered)
          {
             ItemStack stack = cobbleStack;
             cobbleStack = null;
             EntityItem entityItem = new EntityItem(worldObj, xCoord + 0.5f, yCoord + 1, zCoord + 0.5f, stack);
             entityItem.delayBeforeCanPickup = 5;
             entityItem.motionX = 0f;
             entityItem.motionY = LAUNCH_Y_VELOCITY;
             entityItem.motionZ = 0f;
             worldObj.spawnEntityInWorld(entityItem);
          }
          
          if(cobbleStack == null && isPowered && lavaCount > 0 && waterCount > 0)
          {
             cobbleStack = new ItemStack(Item.getItemFromBlock(Blocks.cobblestone), (lavaCount + waterCount )/2);
          }
          
          tickCount = 0;
          lavaCount = 0;
          waterCount = 0;
       }
       else if(tickCount == TICK_REDSTONE_CHECK)
       {
          isPowered = false;
          /**
           *   @note this next block was taken from Modular Systems by PaulJoda
           *   https://github.com/TeamCoS/Modular-Systems/blob/28044dbcd1aefc92ebd19f36a606858f9364f0d6/src/main/java/com/pauljoda/modularsystems/core/tiles/DummyRedstoneInput.java#L40
           **/
          for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
          {
             int weakPower = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ).isProvidingWeakPower(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir.ordinal());
             int strongPower = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ).isProvidingStrongPower(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir.ordinal());
             if (weakPower > 0 || strongPower > 0)
             {
                isPowered = true;
             }
         }
          
          
       }
       else if(tickCount  == TICK_X_CHECK)
       {
          for( int xDelta : posDeltas)
          {
             Block block = worldObj.getBlock(xCoord + xDelta, yCoord, zCoord);
             lavaCount += (block == Blocks.lava) ? 1 : 0;
             waterCount += (block == Blocks.water) ? 1 : 0;
          }
       }
       else if(tickCount == TICK_Y_CHECK)
       {
          Block block = worldObj.getBlock(xCoord, yCoord + 1, zCoord);
          if(block != null && block.isSideSolid(worldObj, xCoord,yCoord + 1, zCoord, ForgeDirection.DOWN))
          {
             isCovered = true;
          }
          else
          {
             isCovered = false;
          }
       }
       else if(tickCount == TICK_Z_CHECK)
       {
          for( int zDelta : posDeltas)
          {
             Block block = worldObj.getBlock(xCoord, yCoord, zCoord + zDelta);
             lavaCount += (block == Blocks.lava) ? 1 : 0;
             waterCount += (block == Blocks.water) ? 1 : 0;
          }
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
         ItemStack stack = cobbleStack;
         cobbleStack = null;
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
