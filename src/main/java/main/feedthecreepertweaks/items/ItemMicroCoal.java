package main.feedthecreepertweaks.items;

import java.util.List;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import main.feedthecreepertweaks.ModInformation;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IIcon;

public class ItemMicroCoal extends ItemBase implements IFuelHandler
{
   public final static String UNLOCALIZED_NAME = "microCoal";
   
   private final static int BURN_TIME = 50;
   
   private IIcon[] icons = new IIcon[2];
      
   public ItemMicroCoal()
   {
      super(UNLOCALIZED_NAME);
      this.setHasSubtypes(true);
      GameRegistry.registerFuelHandler(this);
   }
   
   public String getUnlocalizedName(ItemStack stack)
   {
       return (stack.getItemDamage() == 1 ? "item." + ModInformation.ID + "." + "microCharcoal" : getUnlocalizedName());
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(Item item, CreativeTabs creativeTab, List itemStacks)
   {
      itemStacks.add(new ItemStack(item, 1, 0));
      itemStacks.add(new ItemStack(item, 1, 1));
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int meta)
   {
      if(meta < 2)
      {
         return icons[meta];
      }
      return icons[0];
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_)
   {
      this.icons[0] = p_94581_1_.registerIcon(ModInformation.ID + ":microCoal");
      this.icons[1] = p_94581_1_.registerIcon(ModInformation.ID + ":microCharcoal");
   }
   
   @Override
   public int getBurnTime(ItemStack fuel)
   {
      if(fuel.getItem() instanceof ItemMicroCoal)
      {
         return BURN_TIME;
      }
      else
      {
         return 0;
      }
   }

}
