package TheJudgementMod;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	
	public final static String EXT_PROP_NAME = "ExtendedPlayer";

	private final EntityPlayer player;

	private int currentKarma, maxKarma;

	public ExtendedPlayer(EntityPlayer player) {
		this.player = player;
		this.currentKarma = 1;
		this.maxKarma = 500;
	}

	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(player));
	}

	public static final ExtendedPlayer get(EntityPlayer player) {
		return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();

		properties.setInteger("CurrentKarma", this.currentKarma);
		properties.setInteger("MaxKarma", this.maxKarma);

		compound.setTag(EXT_PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound
				.getTag(EXT_PROP_NAME);
		this.currentKarma = properties.getInteger("CurrentMana");
		this.maxKarma = properties.getInteger("MaxMana");
		System.out.println("[JudgementMod PROPS] Karma from NBT: " + this.currentKarma + "/" + this.maxKarma);
	}

	@Override
	public void init(Entity entity, World world) {
	}
	

	public boolean consumeKarma(int amount)
	{
		boolean sufficient = amount <= this.currentKarma;
		this.currentKarma -= (amount < this.currentKarma ? amount : this.currentKarma);
		return sufficient;
	}
	

	public void replenishKarma(int amount) {
		this.currentKarma =+ amount;
	}

	public int getMaxKarma() {
		return this.maxKarma;
	}

	public int getCurrentKarma() {
		return this.currentKarma;
	}
	


}