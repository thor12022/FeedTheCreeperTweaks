package main.feedthecreepertweaks;

/*
 * Check all the classes for (hopefully) detailed descriptions of what it does. There will also be tidbits of comments throughout the codebase.
 * If you wish to add a description to a class, or extend/change an existing one, submit a PR with your changes.
 */

//import biomesoplenty.api.content.BOPCBiomes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import main.feedthecreepertweaks.blocks.BlockRecipeRegistry;
import main.feedthecreepertweaks.blocks.BlockRegistry;
import main.feedthecreepertweaks.client.gui.CreativeTabBaseMod;
import main.feedthecreepertweaks.client.gui.GuiHandler;
import main.feedthecreepertweaks.handlers.MetallurgyHandler;
import main.feedthecreepertweaks.handlers.PigmanAgroHandler;
import main.feedthecreepertweaks.handlers.ProgressiveAutomationHandler;
import main.feedthecreepertweaks.items.ItemRecipeRegistry;
import main.feedthecreepertweaks.items.ItemRegistry;
import main.feedthecreepertweaks.potions.PotionRegistry;
import main.feedthecreepertweaks.proxies.CommonProxy;
import main.feedthecreepertweaks.util.EventHandler;
import main.feedthecreepertweaks.util.OreDictHandler;
import main.feedthecreepertweaks.util.TextHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import scala.Array;

@Mod(modid = ModInformation.ID, name = ModInformation.NAME, version = ModInformation.VERSION, dependencies = ModInformation.DEPEND)
public class FeedTheCreeperTweaks
{

   @SidedProxy(clientSide = ModInformation.CLIENTPROXY, serverSide = ModInformation.COMMONPROXY)
   public static CommonProxy proxy;

   public static CreativeTabs tabBaseMod = new CreativeTabBaseMod(ModInformation.ID + ".creativeTab");
   public static Logger logger = LogManager.getLogger(ModInformation.NAME);
   
   private PigmanAgroHandler pigmanAgroHandler;

   @Mod.Instance
   public static FeedTheCreeperTweaks instance;

   @Mod.EventHandler
   public void preInit(FMLPreInitializationEvent event)
   {
      logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.load.preInit"));

      ConfigHandler.init(event.getModConfigurationDirectory());

      ItemRegistry.registerItems();
      BlockRegistry.registerBlocks();
      PotionRegistry.registerPotions();
      
      ProgressiveAutomationHandler.preinit(event);
      MetallurgyHandler.preinit(event);

      OreDictHandler.registerOreDict();
      

      pigmanAgroHandler = PigmanAgroHandler.create();
      
      FMLCommonHandler.instance().bus().register(new EventHandler());
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
   }

   @Mod.EventHandler
   public void init(FMLInitializationEvent event)
   {
      logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.load.init"));

      ItemRecipeRegistry.registerItemRecipes();
      BlockRecipeRegistry.registerBlockRecipes();

      ProgressiveAutomationHandler.init(event);
      MetallurgyHandler.init(event);
      
      pigmanAgroHandler.init(event);
   }

   @Mod.EventHandler
   public void postInit(FMLPostInitializationEvent event)
   {
      logger.info(TextHelper.localize("info." + ModInformation.ID + ".console.load.postInit"));
   }

   @Mod.EventHandler
   public void onFMLServerStartedEvent(FMLServerStartedEvent event)
   {
   }
}
