package main.feedthecreepertweaks.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Range;

public class MultiRange
{
   private List<Range<Integer>> ranges= new ArrayList<Range<Integer>>();
   private String stringRep;
   
   /**
    * @param rangeList list of number ranges, e.g. "-1,2,4-9"
    */
   public MultiRange(String rangeList)
   {
      stringRep = rangeList;
      if(rangeList.length() > 0)
      {
         String[] commaSeperated = rangeList.split(",");
         for(String range : commaSeperated)
         {
            int seperatorPos = range.lastIndexOf("-");
            int firstNum = 0, lastNum = 0;
            if(seperatorPos > 0 && range.length() - 1 > seperatorPos)
            {
               firstNum = Integer.parseInt(range.substring(0, seperatorPos));
               lastNum = Integer.parseInt(range.substring(seperatorPos + 1));
               ranges.add(Range.between(firstNum, lastNum));
            }
            else
            {
                ranges.add(Range.is(Integer.parseInt(range)));
            }
         }
      }
   }
   
   public boolean contains(int number)
   {
      for( Range<Integer> range : ranges)
      {
         if( range.contains(number))
         {
            return true;
         }
      }
      return false;
   }
   
   @Override
   public String toString()
   {
      return stringRep;
   }
}
