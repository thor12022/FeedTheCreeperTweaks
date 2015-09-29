package main.feedthecreepertweaks.handlers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.teammetallurgy.metallurgy.api.IMetalSet;
import com.teammetallurgy.metallurgy.api.MetallurgyApi;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.metal.MetalScrollBarUI;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import main.feedthecreepertweaks.ConfigHandler;
import main.feedthecreepertweaks.FeedTheCreeperTweaks;
import main.feedthecreepertweaks.ModInformation;
import main.feedthecreepertweaks.blocks.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;


public class PigmanAgroHandler
{
   private static final String configSectionName = "PigmanAgroHandler";
   private int       agroRange = 32;
   private boolean   metallurgyCompatability = true;
   
   private Multimap<Integer, Integer> idToMetadataMap = HashMultimap.create();
   
   private PigmanAgroHandler()
   {}
   
   public static PigmanAgroHandler create()
   {
      FeedTheCreeperTweaks.logger.debug("Registering Zombie Pigmen Agro-er");
      PigmanAgroHandler pigmanAgroHandler =  new PigmanAgroHandler();
      pigmanAgroHandler.syncConfig();
      pigmanAgroHandler.readJson();
      pigmanAgroHandler.metallurgyCompatibility();
      MinecraftForge.EVENT_BUS.register(pigmanAgroHandler);
      return pigmanAgroHandler;
   }
   
   private void syncConfig()
   {
      agroRange = ConfigHandler.config.getInt("agroRange", configSectionName, agroRange, 0, 128, "Too large and you may see performance issues");
      metallurgyCompatability = ConfigHandler.config.getBoolean("metallurgyCompatability", configSectionName, metallurgyCompatability, "");
      ConfigHandler.config.save();
   }
   
   private void readJson()
   {
      File jsonFile = new File(ConfigHandler.configDirectory + File.separator + ModInformation.CHANNEL + File.separator + getClass().getSimpleName() + ".json");
      try
      {
         if(!jsonFile.exists())
         {
            FileUtils.writeStringToFile(jsonFile, "[\n   {\n      \"BlockName\" : \"glowstone\"\n   }\n]");
         }
         JsonElement rootElement = new JsonParser().parse(FileUtils.readFileToString(jsonFile));
         JsonArray rootArray = rootElement.getAsJsonArray();
         for(JsonElement jElement : rootArray)
         {
            JsonObject jObject = jElement.getAsJsonObject();
            String blockName = jObject.get("BlockName").getAsString();
            int metaData = 0;
            try
            {
               metaData = jElement.getAsJsonObject().getAsJsonObject("MetaData").getAsInt();
            }
            catch(Exception e)
            {}
            idToMetadataMap.put(Block.getIdFromBlock(Block.getBlockFromName(blockName)), metaData);
         }
      }
      catch(Exception exp)
      {
         FeedTheCreeperTweaks.logger.warn("PigmanAgroHandler cannot read " + jsonFile.toString() + " : " + exp);
      }
   }

   private void metallurgyCompatibility()
   {
      if(metallurgyCompatability)
      {
         IMetalSet metalSet = MetallurgyApi.getMetalSet("nether");
         for( String metal : metalSet.getMetalNames())
         {
            ItemStack metalOreStack = metalSet.getOre(metal);
            if(metalOreStack != null)
            {
               Block metalOreBlock = Block.getBlockFromItem(metalOreStack.getItem());
               idToMetadataMap.put(Block.getIdFromBlock(metalOreBlock), metalOreStack.getItemDamage());
            }
         }
      }
   }
   
   @SubscribeEvent
   public void blockBroken(HarvestDropsEvent event)
   {
      if(!event.isSilkTouching)
      {
         if(idToMetadataMap.containsEntry(Block.getIdFromBlock(event.block), event.blockMetadata))
         {
            if(event.harvester == null || event.harvester.capabilities.isCreativeMode)
            {
               return;
            }
            List<EntityPigZombie> list = event.world.getEntitiesWithinAABB(EntityPigZombie.class,
                                                                           AxisAlignedBB.getBoundingBox(event.x - agroRange, 
                                                                                                        event.y - agroRange, 
                                                                                                        event.z - agroRange, 
                                                                                                        event.x + agroRange + 1, 
                                                                                                        event.y + agroRange + 1, 
                                                                                                        event.z + agroRange + 1));
            for(EntityPigZombie entitypigzombie : list)
            {
               try
               {
                  Method becomeAngryMethod = entitypigzombie.getClass().getDeclaredMethod("becomeAngryAt", Entity.class);
                  becomeAngryMethod.setAccessible(true);
                  becomeAngryMethod.invoke(entitypigzombie, (Entity)event.harvester);
               }
               catch(Exception exp)
               {
                  FeedTheCreeperTweaks.logger.debug("Error occured angering Zombie Pigmen:" + exp);
               }
               
            }
         }
      }
   }
}
