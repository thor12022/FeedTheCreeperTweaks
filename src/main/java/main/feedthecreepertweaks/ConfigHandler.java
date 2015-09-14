package main.feedthecreepertweaks;

/*
 * Creation and usage of the config file.
 */

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler
{

   public static Configuration config;
   
   // Sections to add to the config
   public static final String generalSection = "General";
   public static final String progressiveAutomationSection = "Progressive Automation";
   public static final String tinkersConstructSection = "Tinkers' Construct";
   public static final String buildCraftSection = "BuildCraft";

   // Options in the config
   public static boolean doDebug = false;
   public static boolean progressiveAutomationToolOverride = true;
   
   public static void init(File file)
   {
      config = new Configuration(file);
      syncConfig();
   }

   public static void syncConfig()
   {
      doDebug = config.getBoolean("Debug Information",generalSection, doDebug, "");
      
      progressiveAutomationToolOverride = config.getBoolean("Progressive Automation Tool Override",progressiveAutomationSection, progressiveAutomationToolOverride, "");
      
      config.save();
   }
}
