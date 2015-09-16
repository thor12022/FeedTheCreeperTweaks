package main.feedthecreepertweaks.modhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.teammetallurgy.metallurgy.api.IMetalInfo;
import com.teammetallurgy.metallurgy.api.IMetalSet;
import com.teammetallurgy.metallurgy.api.MetallurgyApi;

import org.apache.commons.lang3.Range;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import main.feedthecreepertweaks.ConfigHandler;
import main.feedthecreepertweaks.FeedTheCreeperTweaks;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class MetallurgyHandler

{
   private static MetallurgyHandler instance = new MetallurgyHandler();
   

   private static String configSection = "Metallurgy";
   
   private static class MetalSpawningInfo
   {
      public int weaponSpawnWeight = 1;
      public int armourSpawnWeight = 1;
      
      public String metalName;
      public String metalSetName;
      
      private Range<Integer> weaponSpawnRange = Range.is(-1);
      private Range<Integer> armourSpawnRange = Range.is(-1);
      
      private static int weaponMaxRandNum = 0;
      private static int armourMaxRandNum = 0;
      
      private static final String weaponSpawnWeightString = "weaponSpawnWeight";
      private static final String armourSpawnWeightString = "armourSpawnWeight";
      private static final String dropChanceString = "dropChance";

      private MetalSpawningInfo(IMetalInfo metal, String metalSet)
      {
         metalSetName = metalSet;
         metalName = metal.getName();
         if(metal.haveArmor())
         {
            int defaultArmourWeight = 100 - ((metal.getArmorEnchantability() + metal.getArmorMultiplier() + metal.getArmorDamageReduction()[0] * 4));
            if( defaultArmourWeight <= 1)
            {
               defaultArmourWeight = 1;
            }
            else
            {
               defaultArmourWeight = (int) Math.pow( defaultArmourWeight * 0.1f, 5);
            }
            armourSpawnWeight = ConfigHandler.config.getInt(armourSpawnWeightString, configSection + "." + metalSetName + "." + metalName, defaultArmourWeight, 0, 100000, "");
            if(armourSpawnWeight > 0)
            {
               armourSpawnRange = Range.between(armourMaxRandNum + 1, armourMaxRandNum + armourSpawnWeight);
               armourMaxRandNum += armourSpawnWeight;
            }
         }

         if(metal.haveTools())
         {
            int defaultWeaponsWeight = 350 - (metal.getToolDurability()/5) - metal.getToolEncantabilty() - (metal.getToolDamage()*5);
            if( defaultWeaponsWeight <= 1)
            {
               defaultWeaponsWeight = 1;
            }
            else
            {
               defaultWeaponsWeight = (int) Math.pow( defaultWeaponsWeight * 0.1f, 3);
            }
            weaponSpawnWeight = ConfigHandler.config.getInt(weaponSpawnWeightString, configSection + "." + metalSetName + "." + metalName, defaultWeaponsWeight, 0, 100000, "");
            if(weaponSpawnWeight > 0)
            {
               weaponSpawnRange = Range.between(weaponMaxRandNum + 1, weaponMaxRandNum + weaponSpawnWeight);
               weaponMaxRandNum += weaponSpawnWeight;
            }
         }
      }

      public boolean isValidWeaponSpawn(int testWeight)
      {
         return weaponSpawnRange.contains(testWeight);
      }

      public boolean isValidArmourSpawn(int testWeight)
      {
         return armourSpawnRange.contains(testWeight);
      }
   }

   private static List<MetalSpawningInfo> metalList = new LinkedList<MetalSpawningInfo>();
   private static Map<String, String> metalSetDimensionMap = new HashMap<String, String>();
   private static final Map<String, String> metalSetDefaultDimensionMap;
   static
   {
      metalSetDefaultDimensionMap = new HashMap<String, String>();
      metalSetDefaultDimensionMap.put("base", "0,2-100000");
      metalSetDefaultDimensionMap.put("precious", "0,2-100000");
      metalSetDefaultDimensionMap.put("fantasy", "0,2-100000");
      metalSetDefaultDimensionMap.put("nether", "-1");
      metalSetDefaultDimensionMap.put("ender", "1");
      metalSetDefaultDimensionMap.put("utility", "");
   }
   
   private boolean doMobSpawns = true;
   private float   mobSpawnChance = 0.005f; 
   
   public static void preinit(FMLPreInitializationEvent event)
   {
      populateMetals();
   }

   public static void init(FMLInitializationEvent event)
   {
      instance.doMobSpawns = ConfigHandler.config.getBoolean("doMobSpawn",configSection, instance.doMobSpawns, "Mobs Spawn With Metallurgy Weapons/Armour");
      instance.mobSpawnChance = ConfigHandler.config.getFloat("mobSpawnChance",configSection, instance.mobSpawnChance, 0f, 1f, "Chance mobs will spawn with Metallurgy Weapons/Armour");
      
      ConfigHandler.config.save();
      
      MinecraftForge.EVENT_BUS.register(instance);
   }
   
   private static void populateMetals()
   {
      for(String setName : MetallurgyApi.getSetNames())
      {
         boolean useMetalSet = ConfigHandler.config.getBoolean("UseMetalSet", configSection + "." + setName, true, "If false, all metals in set will be ignored, any already spawned will stay");
         metalSetDimensionMap.put(setName, ConfigHandler.config.getString("SpawnMobItemsInDimensions", 
                                                                          configSection + "." + setName, 
                                                                          metalSetDefaultDimensionMap.get(setName), 
                                                                          "Dimensions where this metal set will spawn as armour and weapons on mobs"));
         if(useMetalSet)
         {
            IMetalSet metalSet = MetallurgyApi.getMetalSet(setName);
            for(String metalName : metalSet.getMetalNames())
            {
               IMetalInfo metalInfo = metalSet.getMetal(metalName);
               if(metalInfo.haveTools() || metalInfo.haveArmor())
               {
                  metalList.add(new MetalSpawningInfo(metalInfo, setName));
               }
            }
         }
      }
      ConfigHandler.config.save();
   }
   
   @SubscribeEvent
   public void onLivingSpawn(LivingSpawnEvent event)
   {
      // Only give armour and weapons to Zombies and skeletons
      if(event.entityLiving instanceof EntityZombie || event.entityLiving instanceof EntitySkeleton)
      {
         // only give Zombies swords
         if(((event.entityLiving instanceof EntityZombie) ? event.world.rand.nextFloat() : 1) < mobSpawnChance)
         {
            MetalSpawningInfo metal = getRandomWeaponMetal(event.entityLiving.dimension, event.world.rand);
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(0, MetallurgyApi.getMetalSet(metal.metalSetName).getSword(metal.metalName));
            }
         }

         // Helmet
         if(event.world.rand.nextFloat() < mobSpawnChance)
         {
            MetalSpawningInfo metal = getRandomArmourMetal(event.entityLiving.dimension, event.world.rand);
            if(metal != null && ConfigHandler.numberIsInSelection(event.entityLiving.dimension, metalSetDimensionMap.get(metal.metalSetName)))
            {
               event.entityLiving.setCurrentItemOrArmor(4, MetallurgyApi.getMetalSet(metal.metalSetName).getHelmet(metal.metalName));
            }
         }
         
         // Chestplate
         if(event.world.rand.nextFloat() < mobSpawnChance)
         {
            MetalSpawningInfo metal = getRandomArmourMetal(event.entityLiving.dimension, event.world.rand);
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(3, MetallurgyApi.getMetalSet(metal.metalSetName).getChestplate(metal.metalName));
            }
         }

         // Leggings
         if(event.world.rand.nextFloat() < mobSpawnChance)
         {
            MetalSpawningInfo metal = getRandomArmourMetal(event.entityLiving.dimension, event.world.rand);
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(2, MetallurgyApi.getMetalSet(metal.metalSetName).getLeggings(metal.metalName));
            }
         }

         // Boots
         if(event.world.rand.nextFloat() < mobSpawnChance)
         {
            MetalSpawningInfo metal = getRandomArmourMetal(event.entityLiving.dimension, event.world.rand);
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(1, MetallurgyApi.getMetalSet(metal.metalSetName).getBoots(metal.metalName));
            }
         }
         
      }
   }
   
   private static MetalSpawningInfo getRandomWeaponMetal(int dim, Random rand)
   {
      for(MetalSpawningInfo metalInfo : metalList)
      {
         if( ConfigHandler.numberIsInSelection(dim, metalSetDimensionMap.get(metalInfo.metalSetName)) && 
               metalInfo.isValidWeaponSpawn(rand.nextInt(MetalSpawningInfo.weaponMaxRandNum)))
         {
            return metalInfo;
         }
      }
      return null;
   }

   private static MetalSpawningInfo getRandomArmourMetal(int dim, Random rand)
   {
      for(MetalSpawningInfo metalInfo : metalList)
      {
         if( ConfigHandler.numberIsInSelection(dim, metalSetDimensionMap.get(metalInfo.metalSetName)) && 
             metalInfo.isValidArmourSpawn(rand.nextInt(MetalSpawningInfo.armourMaxRandNum)))
         {
            return metalInfo;
         }
      }
      return null;
   }
}
