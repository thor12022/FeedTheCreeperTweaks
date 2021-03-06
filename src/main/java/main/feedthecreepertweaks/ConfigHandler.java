package main.feedthecreepertweaks;

/*
 * Creation and usage of the config file.
 */

import net.minecraftforge.common.config.Configuration;

import java.io.File;

import org.apache.commons.lang3.Range;

public class ConfigHandler
{

   public static Configuration config;
   public static String configDirectory;
   
   // Sections to add to the config
   public static final String generalSection = "General";
   public static final String progressiveAutomationSection = "Progressive Automation";
   public static final String tinkersConstructSection = "Tinkers' Construct";
   public static final String buildCraftSection = "BuildCraft";

   // Options in the config
   public static boolean doDebug = false;
   public static boolean progressiveAutomationToolOverride = true;
   public static boolean strongholdWand = false;
   
   public static void init(File configDir)
   {
      configDirectory = configDir.getAbsolutePath();
      config = new Configuration( new File(configDirectory + File.separator + ModInformation.CHANNEL + File.separator + ModInformation.CHANNEL + ".cfg"));
      syncConfig();
   }

   public static void syncConfig()
   {
      doDebug = config.getBoolean("Debug Information",generalSection, doDebug, "");
      
      progressiveAutomationToolOverride = config.getBoolean("Progressive Automation Tool Override",progressiveAutomationSection, progressiveAutomationToolOverride, "");
      strongholdWand = config.getBoolean("Stronghold Wand Enabled",generalSection, strongholdWand, "Not fully implemented");
      
      config.save();
   }
}
