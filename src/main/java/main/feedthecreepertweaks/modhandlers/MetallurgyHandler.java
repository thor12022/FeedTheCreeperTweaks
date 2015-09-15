package main.feedthecreepertweaks.modhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
   
   private static class MetalSpawningInfo
   {
      public int weaponSpawnWeight = 1;
      public int armourSpawnWeight = 1;
      public float dropChance = 0.0001f;
      
      public String metalName;
      public String metalSetName;
      
      private Range<Integer> weaponSpawnRange = Range.is(-1);
      private Range<Integer> armourSpawnRange = Range.is(-1);
      
      private static int totalWeaponWeight = 0;
      private static int totalArmourWeight = 0;
      
      private static final String weaponSpawnWeightString = "weaponSpawnWeight";
      private static final String armourSpawnWeightString = "armourSpawnWeight";
      private static final String dropChanceString = "dropChance";
      
      private static List<MetalSpawningInfo> metalList = new LinkedList<MetalSpawningInfo>();
      
      public static void AddMetal(IMetalInfo metal, String metalSet)
      {
         metalList.add(new MetalSpawningInfo(metal, metalSet));
      }
      
      private MetalSpawningInfo(IMetalInfo metal, String metalSet)
      {
         metalSetName = metalSet;
         metalName = metal.getName();
         if(metal.haveArmor())
         {
            int defaultArmourWeight = 100 - ((metal.getArmorEnchantability() + metal.getArmorMultiplier() + metal.getArmorDamageReduction()[0] * 4));
            if( defaultArmourWeight < 1)
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
               armourSpawnRange = Range.between(totalArmourWeight + 1, totalArmourWeight + armourSpawnWeight);
               totalArmourWeight += armourSpawnWeight;
            }
         }

         if(metal.haveTools())
         {
            int defaultWeaponsWeight = 350 - (metal.getToolDurability()/5) - metal.getToolEncantabilty() - (metal.getToolDamage()*5);
            if( defaultWeaponsWeight < 1)
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
               weaponSpawnRange = Range.between(totalWeaponWeight + 1, totalWeaponWeight + weaponSpawnWeight);
               totalWeaponWeight += weaponSpawnWeight;
            }
         }
         dropChance = ConfigHandler.config.getFloat(dropChanceString, configSection + "." + metalSetName + "." + metalName, dropChance, 0f, 1, "");

         ConfigHandler.config.save();
      }

      public static MetalSpawningInfo getRandomWeaponMetal(int testWeight)
      {
         for(MetalSpawningInfo metalInfo : metalList)
         {
            if( metalInfo.isValidWeaponSpawn(testWeight))
            {
               return metalInfo;
            }
         }
         return null;
      }

      public static MetalSpawningInfo getRandomArmourMetal(int testWeight)
      {
         for(MetalSpawningInfo metalInfo : metalList)
         {
            if( metalInfo.isValidArmourSpawn(testWeight))
            {
               return metalInfo;
            }
         }
         return null;
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
   private static String configSection = "Metallurgy";
   
   private boolean doMobSpawns = true;
   private float   mobSpawnChance = 0.01f; 
   
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
         IMetalSet metalSet = MetallurgyApi.getMetalSet(setName);
         for(String metalName : metalSet.getMetalNames())
         {
            IMetalInfo metalInfo = metalSet.getMetal(metalName);
            if(metalInfo.haveTools() || metalInfo.haveArmor())
            {
               MetalSpawningInfo.AddMetal(metalInfo, setName);
            }
         }
      }
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
            MetalSpawningInfo metal = MetalSpawningInfo.getRandomWeaponMetal(event.world.rand.nextInt(MetalSpawningInfo.totalWeaponWeight));
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(0, MetallurgyApi.getMetalSet(metal.metalSetName).getSword(metal.metalName));
            }
         }

         // Helmet
         if(event.world.rand.nextFloat() < mobSpawnChance)
         {
            MetalSpawningInfo metal = MetalSpawningInfo.getRandomArmourMetal(event.world.rand.nextInt(MetalSpawningInfo.totalArmourWeight));
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(1, MetallurgyApi.getMetalSet(metal.metalSetName).getHelmet(metal.metalName));
            }
         }
         
         // Chestplate
         if(event.world.rand.nextFloat() < mobSpawnChance)
         {
            MetalSpawningInfo metal = MetalSpawningInfo.getRandomArmourMetal(event.world.rand.nextInt(MetalSpawningInfo.totalArmourWeight));
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(2, MetallurgyApi.getMetalSet(metal.metalSetName).getChestplate(metal.metalName));
            }
         }

         // Leggings
         if(event.world.rand.nextFloat() < mobSpawnChance)
         {
            MetalSpawningInfo metal = MetalSpawningInfo.getRandomArmourMetal(event.world.rand.nextInt(MetalSpawningInfo.totalArmourWeight));
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(3, MetallurgyApi.getMetalSet(metal.metalSetName).getLeggings(metal.metalName));
            }
         }

         // Boots
         if(event.world.rand.nextFloat() < mobSpawnChance)
         {
            MetalSpawningInfo metal = MetalSpawningInfo.getRandomArmourMetal(event.world.rand.nextInt(MetalSpawningInfo.totalArmourWeight));
            if(metal != null)
            {
               event.entityLiving.setCurrentItemOrArmor(4, MetallurgyApi.getMetalSet(metal.metalSetName).getBoots(metal.metalName));
            }
         }
         
      }
   }
}
