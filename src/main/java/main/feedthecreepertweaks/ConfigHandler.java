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
   
   /**
    * @param selection list of number ranges, e.g. "-1,2,4-9"
    * @todo this should throw an exception, so we can know where the error is
    */
   public static boolean numberIsInSelection(int num, String selection)
   {
      boolean retValue = false;
      try
      {
         String[] commaSeperated = selection.split(",");
         for(String range : commaSeperated)
         {
            int seperatorPos = range.lastIndexOf("-");
            int firstNum = 0, lastNum = 0;
            if(seperatorPos > 0 && range.length() > seperatorPos)
            {
               firstNum = Integer.parseInt(range.substring(0, seperatorPos - 1));
               lastNum = Integer.parseInt(range.substring(seperatorPos + 1));
               retValue = Range.between(firstNum, lastNum).contains(num);
            }
            else
            {
               retValue = Integer.parseInt(range) == num;
            }
            if(retValue)
            {
               break;
            }
         }
      }
      catch( Exception e)
      {
         FeedTheCreeperTweaks.logger.warn("Badly formatted selection: \"" + selection + "\"");
      }
      return retValue;
   }
}
