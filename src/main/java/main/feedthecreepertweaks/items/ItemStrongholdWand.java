package main.feedthecreepertweaks.items;

import java.lang.reflect.Field;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ItemStrongholdWand extends ItemBase
{
   public final static String UNLOCALIZED_NAME = "strongholdWand";
   private final static Random RANDOM = new Random();
   
   public ItemStrongholdWand()
   {
      super(UNLOCALIZED_NAME, UNLOCALIZED_NAME);
      
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onplayerInteract(PlayerInteractEvent event)
   {
      if(event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
      {
         return;
      }
      if(!event.entity.worldObj.isRemote && event.entityPlayer.getHeldItem().getItem() instanceof ItemStrongholdWand)
      {
         MapGenStronghold mapGenStronghold;
         ChunkProviderGenerate chunkProvider;
         try
         {
            chunkProvider = (ChunkProviderGenerate)((ChunkProviderServer) event.entity.worldObj.getChunkProvider()).currentChunkProvider;
            //! @todo get the obfuscated field name
            Field field = chunkProvider.getClass().getDeclaredField("strongholdGenerator");
            field.setAccessible(true);
            mapGenStronghold = (MapGenStronghold) field.get(chunkProvider);
         }
         catch(Exception excp)
         {
            return;
         }
         
         // Would need to get all the blocks in the chunk as Block[] for this to maybe start to kinda work
         //mapGenStronghold.func_151539_a(chunkProvider, event.entity.worldObj, (int)Math.round(event.entity.posX / 16), (int)Math.round(event.entity.posZ /16), p_151539_5_);
         // Would need to do this over the surrounding chunks I think
         //mapGenStronghold.generateStructuresInChunk(event.entity.worldObj, RANDOM, );
         
         return;
      }
   }
   
}
