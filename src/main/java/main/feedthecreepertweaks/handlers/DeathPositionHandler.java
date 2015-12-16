package main.feedthecreepertweaks.handlers;

import java.util.Formatter;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import main.feedthecreepertweaks.ConfigHandler;
import main.feedthecreepertweaks.FeedTheCreeperTweaks;
import main.feedthecreepertweaks.ModInformation;
import main.feedthecreepertweaks.util.TextHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class DeathPositionHandler
{

   private static final String configSection = "DeathPosition";
   
   private boolean alertPlayer = true;
   
   public void preInit(FMLPreInitializationEvent event)
   {
      alertPlayer = ConfigHandler.config.getBoolean("alertPlayer",configSection, alertPlayer, "Alert Player of position in chat");
   }
   
   public void init(FMLInitializationEvent event)
   {
      MinecraftForge.EVENT_BUS.register(this);
   }
   
   @SubscribeEvent
   public void playerDeath(LivingDeathEvent event)
   {
      if(!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
      {
         String coords = String.format("%d,%d,%d", 
                                       Math.round(event.entity.posX),
                                       Math.round(event.entity.posY),
                                       Math.round(event.entity.posZ));
                                       String msg = String.format("%s %s %s", 
                                                                  ((EntityPlayer)event.entity).getCommandSenderName(),
                                                                  TextHelper.localize("info." + ModInformation.ID + ".console.load.deathMessageDied"),
                                                                  coords);
                                       FeedTheCreeperTweaks.logger.info(msg);
                                       if(alertPlayer)
                                       {
                                          msg = TextHelper.localize("info." + ModInformation.ID + ".chat.deathMessage") + " " + coords;
                                          ((EntityPlayer)event.entity).addChatComponentMessage(new ChatComponentText(msg));
                                       }
      }
   }
}
