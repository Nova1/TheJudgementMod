package TheJudgementMod.network;

import javax.swing.event.DocumentEvent.EventType;
import TheJudgementMod.ExtendedPlayer;
import TheJudgementMod.entity.villager.EntitymodVillager;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class TJMEventHandler extends ForgeEventFactory {

	@EventHandler
	public void load(FMLInitializationEvent event) {

		MinecraftForge.EVENT_BUS.register(new TJMEventHandler());
		FMLCommonHandler.instance().bus().register(new TJMEventHandler());
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME) == null) event.entity.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer((EntityPlayer) event.entity));
	}

	@SubscribeEvent
	public void EntityJoinWorldEvent(LivingSpawnEvent.SpecialSpawn event) {
		EntityLivingBase living = event.entityLiving;
		if (living instanceof EntityVillager) {
			EntitymodVillager mVillager = new EntitymodVillager(living.worldObj);
			spawnEntityLiving(living.posX, living.posY + 1, living.posZ, mVillager, living.worldObj);
			living.setDead();
		}
	}

	public static void spawnEntityLiving(double x, double y, double z, EntityLiving entity, World world) {
		if (!world.isRemote) {
			entity.setPosition(x, y, z);
			entity.onSpawnWithEgg((IEntityLivingData) null);
			world.spawnEntityInWorld(entity);
		}
	}

}
