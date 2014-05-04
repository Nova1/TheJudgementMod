package TheJudgementMod;

import net.minecraft.entity.player.EntityPlayer;

public class Carma {
	
	public EntityPlayer player;
	public int Carma;
	
	public Carma(int amount, String player) {
		addpointTrading(amount, player);
	}
	
	public void addpointTrading(int amount, String lastBuyingplayer) {
		this.Carma =+ amount;
		player.addStat(TheJudgementMod.carma, 1);
	}
	
	public void addpointBreeding(int amount, String lastBreedingplayer) {
		this.Carma =+ amount;
		player.addStat(TheJudgementMod.carma, 1);
	}
	
}
