package TheJudgementMod.entity.villager.ai;

import TheJudgementMod.entity.villager.EntitymodVillager;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAILookAtTradePlayer extends EntityAIWatchClosest
{
    private final EntitymodVillager theMerchant;
    private static final String __OBFID = "CL_00001593";

    public EntityAILookAtTradePlayer(EntitymodVillager par1EntitymodVillager)
    {
        super(par1EntitymodVillager, EntityPlayer.class, 8.0F);
        this.theMerchant = par1EntitymodVillager;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.theMerchant.isTrading())
        {
            this.closestEntity = this.theMerchant.getCustomer();
            return true;
        }
        else
        {
            return false;
        }
    }
}