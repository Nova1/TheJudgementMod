package TheJudgementMod.entity.villager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import TheJudgementMod.ExtendedPlayer;
import TheJudgementMod.entity.villager.ai.EntityAIFollowGolem;
import TheJudgementMod.entity.villager.ai.EntityAILookAtTradePlayer;
import TheJudgementMod.entity.villager.ai.EntityAIPlay;
import TheJudgementMod.entity.villager.ai.EntityAITradePlayer;
import TheJudgementMod.entity.villager.ai.EntityAIVillagerMate;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitymodVillager extends EntityAgeable implements IMerchant, INpc
{
    private int randomTickDivider;
    private boolean isMating;
    private boolean isPlaying;
    Village villageObj;
    /** This villager's current customer. */
    private EntityPlayer buyingPlayer;
    /** Initialises the MerchantRecipeList.java */
    private MerchantRecipeList buyingList;
    private int timeUntilReset;
    /** addDefaultEquipmentAndRecipies is called if this is true */
    private boolean needsInitilization;
    private int wealth;
    /** Last player to trade with this villager, used for aggressivity. */
    private String lastBuyingPlayer;
    public int playercarma;
    private boolean isLookingForHome;
    private float field_82191_bN;
    /** Selling list of Villagers items. */
    public static final Map villagersSellingList = new HashMap();
    /** Selling list of Blacksmith items. */
    public static final Map blacksmithSellingList = new HashMap();
    private static final String __OBFID = "CL_00001707";
    
    public static ExtendedPlayer Eplayer;

    public EntitymodVillager(World par1World)
    {
        this(par1World, 0);
    }

    public EntitymodVillager(World par1World, int par2)
    {
        super(par1World);
        this.setProfession(par2);
        this.setSize(0.6F, 1.8F);
        this.getNavigator().setBreakDoors(true);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(7, new EntityAIFollowGolem(this));
        this.tasks.addTask(8, new EntityAIPlay(this, 0.32D));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntitymodVillager.class, 5.0F, 0.02F));
        this.tasks.addTask(9, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
    }

    /**
     * Returns true if the newer Entity AI code should be run
     */
    public boolean isAIEnabled()
    {
        return true;
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick()
    {
        if (--this.randomTickDivider <= 0)
        {
            this.worldObj.villageCollectionObj.addVillagerPosition(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.villageObj = this.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);

            if (this.villageObj == null)
            {
                this.detachHome();
            }
            else
            {
                ChunkCoordinates chunkcoordinates = this.villageObj.getCenter();
                this.setHomeArea(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, (int)((float)this.villageObj.getVillageRadius() * 0.6F));

                if (this.isLookingForHome)
                {
                    this.isLookingForHome = false;
                    this.villageObj.setDefaultPlayerReputation(5);
                }
            }
        }

        if (!this.isTrading() && this.timeUntilReset > 0)
        {
            --this.timeUntilReset;

            if (this.timeUntilReset <= 0)
            {
                if (this.needsInitilization)
                {
                    if (this.buyingList.size() > 1)
                    {
                        Iterator iterator = this.buyingList.iterator();

                        while (iterator.hasNext())
                        {
                            MerchantRecipe merchantrecipe = (MerchantRecipe)iterator.next();

                            if (merchantrecipe.isRecipeDisabled())
                            {
                                merchantrecipe.func_82783_a(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                            }
                        }
                    }

                    this.addDefaultEquipmentAndRecipies(1);
                    this.needsInitilization = false;

                    if (this.villageObj != null && this.lastBuyingPlayer != null)
                    {
                        this.worldObj.setEntityState(this, (byte)14);
                        this.villageObj.setReputationForPlayer(this.lastBuyingPlayer, 1);
                    }
                }

                this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
            }
        }

        super.updateAITick();
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
        boolean flag = itemstack != null && itemstack.getItem() == Items.spawn_egg;

        if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !par1EntityPlayer.isSneaking())
        {
            if (!this.worldObj.isRemote)
            {
                this.setCustomer(par1EntityPlayer);
                par1EntityPlayer.displayGUIMerchant(this, this.getCustomNameTag());
                Eplayer.replenishKarma(1);
            }
            Eplayer.replenishKarma(1);
            return true;
        }
        else
        {
            return super.interact(par1EntityPlayer);
        }
    }


	protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Integer.valueOf(0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Profession", this.getProfession());
        par1NBTTagCompound.setInteger("Riches", this.wealth);

        if (this.buyingList != null)
        {
            par1NBTTagCompound.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setProfession(par1NBTTagCompound.getInteger("Profession"));
        this.wealth = par1NBTTagCompound.getInteger("Riches");

        if (par1NBTTagCompound.hasKey("Offers", 10))
        {
            NBTTagCompound nbttagcompound1 = par1NBTTagCompound.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(nbttagcompound1);
        }
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return false;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return this.isTrading() ? "mob.villager.haggle" : "mob.villager.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.villager.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.villager.death";
    }

    public void setProfession(int par1)
    {
        this.dataWatcher.updateObject(16, Integer.valueOf(par1));
    }

    public int getProfession()
    {
        return this.dataWatcher.getWatchableObjectInt(16);
    }

    public boolean isMating()
    {
        return this.isMating;
    }

    public void setMating(boolean par1)
    {
        this.isMating = par1;
    }

    public void setPlaying(boolean par1)
    {
        this.isPlaying = par1;
    }

    public boolean isPlaying()
    {
        return this.isPlaying;
    }

    public void setRevengeTarget(EntityLivingBase par1EntityLivingBase)
    {
        super.setRevengeTarget(par1EntityLivingBase);

        if (this.villageObj != null && par1EntityLivingBase != null)
        {
            this.villageObj.addOrRenewAgressor(par1EntityLivingBase);

            if (par1EntityLivingBase instanceof EntityPlayer)
            {
                byte b0 = -1;

                if (this.isChild())
                {
                    b0 = -3;
                }

                this.villageObj.setReputationForPlayer(par1EntityLivingBase.getCommandSenderName(), b0);

                if (this.isEntityAlive())
                {
                    this.worldObj.setEntityState(this, (byte)13);
                }
            }
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource par1DamageSource)
    {
        if (this.villageObj != null)
        {
            Entity entity = par1DamageSource.getEntity();

            if (entity != null)
            {
                if (entity instanceof EntityPlayer)
                {
                    this.villageObj.setReputationForPlayer(entity.getCommandSenderName(), -2);
                }
                else if (entity instanceof IMob)
                {
                    this.villageObj.endMatingSeason();
                }
            }
            else if (entity == null)
            {
                EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0D);

                if (entityplayer != null)
                {
                    this.villageObj.endMatingSeason();
                }
            }
        }

        super.onDeath(par1DamageSource);
    }

    public void setCustomer(EntityPlayer par1EntityPlayer)
    {
        this.buyingPlayer = par1EntityPlayer;
    }

    public EntityPlayer getCustomer()
    {
        return this.buyingPlayer;
    }

    public boolean isTrading()
    {
        return this.buyingPlayer != null;
    }

    public void useRecipe(MerchantRecipe par1MerchantRecipe)
    {
        par1MerchantRecipe.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());

        if (par1MerchantRecipe.hasSameIDsAs((MerchantRecipe)this.buyingList.get(this.buyingList.size() - 1)))
        {
            this.timeUntilReset = 40;
            this.needsInitilization = true;

            if (this.buyingPlayer != null)
            {
                this.lastBuyingPlayer = this.buyingPlayer.getCommandSenderName();
            }
            else
            {
                this.lastBuyingPlayer = null;
            }
        }

        if (par1MerchantRecipe.getItemToBuy().getItem() == Items.emerald)
        {
            this.wealth += par1MerchantRecipe.getItemToBuy().stackSize;
        }
    }

    public void func_110297_a_(ItemStack par1ItemStack)
    {
        if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20)
        {
            this.livingSoundTime = -this.getTalkInterval();

            if (par1ItemStack != null)
            {
                this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
            }
            else
            {
                this.playSound("mob.villager.no", this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    public MerchantRecipeList getRecipes(EntityPlayer par1EntityPlayer)
    {
        if (this.buyingList == null)
        {
            this.addDefaultEquipmentAndRecipies(1);
        }

        return this.buyingList;
    }

    /**
     * Adjusts the probability of obtaining a given recipe being offered by a villager
     */
    private float adjustProbability(float par1)
    {
        float f1 = par1 + this.field_82191_bN;
        return f1 > 0.9F ? 0.9F - (f1 - 0.9F) : f1;
    }

    /**
     * based on the villagers profession add items, equipment, and recipies adds par1 random items to the list of things
     * that the villager wants to buy. (at most 1 of each wanted type is added)
     */
    private void addDefaultEquipmentAndRecipies(int par1)
    {
        if (this.buyingList != null)
        {
            this.field_82191_bN = MathHelper.sqrt_float((float)this.buyingList.size()) * 0.2F;
        }
        else
        {
            this.field_82191_bN = 0.0F;
        }

        MerchantRecipeList merchantrecipelist;
        merchantrecipelist = new MerchantRecipeList();
        int k;
        label50:

        switch (this.getProfession())
        {
            case 0:
                func_146091_a(merchantrecipelist, Items.wheat, this.rand, this.adjustProbability(0.9F));
                func_146091_a(merchantrecipelist, Item.getItemFromBlock(Blocks.wool), this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.chicken, this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.cooked_fished, this.rand, this.adjustProbability(0.4F));
                func_146089_b(merchantrecipelist, Items.bread, this.rand, this.adjustProbability(0.9F));
                func_146089_b(merchantrecipelist, Items.melon, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.apple, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.cookie, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.shears, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.flint_and_steel, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.cooked_chicken, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.arrow, this.rand, this.adjustProbability(0.5F));

                if (this.rand.nextFloat() < this.adjustProbability(0.5F))
                {
                    merchantrecipelist.add(new MerchantRecipe(new ItemStack(Blocks.gravel, 10), new ItemStack(Items.emerald), new ItemStack(Items.flint, 4 + this.rand.nextInt(2), 0)));
                }

                break;
            case 1:
                func_146091_a(merchantrecipelist, Items.paper, this.rand, this.adjustProbability(0.8F));
                func_146091_a(merchantrecipelist, Items.book, this.rand, this.adjustProbability(0.8F));
                func_146091_a(merchantrecipelist, Items.written_book, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Item.getItemFromBlock(Blocks.bookshelf), this.rand, this.adjustProbability(0.8F));
                func_146089_b(merchantrecipelist, Item.getItemFromBlock(Blocks.glass), this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.compass, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.clock, this.rand, this.adjustProbability(0.2F));

                if (this.rand.nextFloat() < this.adjustProbability(0.07F))
                {
                    Enchantment enchantment = Enchantment.enchantmentsBookList[this.rand.nextInt(Enchantment.enchantmentsBookList.length)];
                    int i1 = MathHelper.getRandomIntegerInRange(this.rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
                    ItemStack itemstack = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, i1));
                    k = 2 + this.rand.nextInt(5 + i1 * 10) + 3 * i1;
                    merchantrecipelist.add(new MerchantRecipe(new ItemStack(Items.book), new ItemStack(Items.emerald, k), itemstack));
                }

                break;
            case 2:
                func_146089_b(merchantrecipelist, Items.ender_eye, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.experience_bottle, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.redstone, this.rand, this.adjustProbability(0.4F));
                func_146089_b(merchantrecipelist, Item.getItemFromBlock(Blocks.glowstone), this.rand, this.adjustProbability(0.3F));
                Item[] aitem = new Item[] {Items.iron_sword, Items.diamond_sword, Items.iron_chestplate, Items.diamond_chestplate, Items.iron_axe, Items.diamond_axe, Items.iron_pickaxe, Items.diamond_pickaxe};
                Item[] aitem1 = aitem;
                int j = aitem.length;
                k = 0;

                while (true)
                {
                    if (k >= j)
                    {
                        break label50;
                    }

                    Item item = aitem1[k];

                    if (this.rand.nextFloat() < this.adjustProbability(0.05F))
                    {
                        merchantrecipelist.add(new MerchantRecipe(new ItemStack(item, 1, 0), new ItemStack(Items.emerald, 2 + this.rand.nextInt(3), 0), EnchantmentHelper.addRandomEnchantment(this.rand, new ItemStack(item, 1, 0), 5 + this.rand.nextInt(15))));
                    }

                    ++k;
                }
            case 3:
                func_146091_a(merchantrecipelist, Items.coal, this.rand, this.adjustProbability(0.7F));
                func_146091_a(merchantrecipelist, Items.iron_ingot, this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.gold_ingot, this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.diamond, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.iron_sword, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.diamond_sword, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.iron_axe, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.diamond_axe, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.iron_pickaxe, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.diamond_pickaxe, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.iron_shovel, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_shovel, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_hoe, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_hoe, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_boots, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_boots, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_helmet, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_helmet, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_chestplate, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_chestplate, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_leggings, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_leggings, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.chainmail_boots, this.rand, this.adjustProbability(0.1F));
                func_146089_b(merchantrecipelist, Items.chainmail_helmet, this.rand, this.adjustProbability(0.1F));
                func_146089_b(merchantrecipelist, Items.chainmail_chestplate, this.rand, this.adjustProbability(0.1F));
                func_146089_b(merchantrecipelist, Items.chainmail_leggings, this.rand, this.adjustProbability(0.1F));
                break;
            case 4:
                func_146091_a(merchantrecipelist, Items.coal, this.rand, this.adjustProbability(0.7F));
                func_146091_a(merchantrecipelist, Items.porkchop, this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.beef, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.saddle, this.rand, this.adjustProbability(0.1F));
                func_146089_b(merchantrecipelist, Items.leather_chestplate, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.leather_boots, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.leather_helmet, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.leather_leggings, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.cooked_porkchop, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.cooked_beef, this.rand, this.adjustProbability(0.3F));
        }

        if (merchantrecipelist.isEmpty())
        {
            func_146091_a(merchantrecipelist, Items.gold_ingot, this.rand, 1.0F);
        }

        Collections.shuffle(merchantrecipelist);

        if (this.buyingList == null)
        {
            this.buyingList = new MerchantRecipeList();
        }

        for (int l = 0; l < par1 && l < merchantrecipelist.size(); ++l)
        {
            this.buyingList.addToListWithCheck((MerchantRecipe)merchantrecipelist.get(l));
        }
    }

    @SideOnly(Side.CLIENT)
    public void setRecipes(MerchantRecipeList par1MerchantRecipeList) {}

    public static void func_146091_a(MerchantRecipeList p_146091_0_, Item p_146091_1_, Random p_146091_2_, float p_146091_3_)
    {
        if (p_146091_2_.nextFloat() < p_146091_3_)
        {
            p_146091_0_.add(new MerchantRecipe(func_146088_a(p_146091_1_, p_146091_2_), Items.emerald));
        }
    }

    private static ItemStack func_146088_a(Item p_146088_0_, Random p_146088_1_)
    {
        return new ItemStack(p_146088_0_, func_146092_b(p_146088_0_, p_146088_1_), 0);
    }

    private static int func_146092_b(Item p_146092_0_, Random p_146092_1_)
    {
        Tuple tuple = (Tuple)villagersSellingList.get(p_146092_0_);
        return tuple == null ? 1 : (((Integer)tuple.getFirst()).intValue() >= ((Integer)tuple.getSecond()).intValue() ? ((Integer)tuple.getFirst()).intValue() : ((Integer)tuple.getFirst()).intValue() + p_146092_1_.nextInt(((Integer)tuple.getSecond()).intValue() - ((Integer)tuple.getFirst()).intValue()));
    }

    public static void func_146089_b(MerchantRecipeList p_146089_0_, Item p_146089_1_, Random p_146089_2_, float p_146089_3_)
    {
        if (p_146089_2_.nextFloat() < p_146089_3_)
        {
            int i = func_146090_c(p_146089_1_, p_146089_2_);
            ItemStack itemstack;
            ItemStack itemstack1;

            if (i < 0)
            {
                itemstack = new ItemStack(Items.emerald, 1, 0);
                itemstack1 = new ItemStack(p_146089_1_, -i, 0);
            }
            else
            {
                itemstack = new ItemStack(Items.emerald, i, 0);
                itemstack1 = new ItemStack(p_146089_1_, 1, 0);
            }

            p_146089_0_.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    private static int func_146090_c(Item p_146090_0_, Random p_146090_1_)
    {
        Tuple tuple = (Tuple)blacksmithSellingList.get(p_146090_0_);
        return tuple == null ? 1 : (((Integer)tuple.getFirst()).intValue() >= ((Integer)tuple.getSecond()).intValue() ? ((Integer)tuple.getFirst()).intValue() : ((Integer)tuple.getFirst()).intValue() + p_146090_1_.nextInt(((Integer)tuple.getSecond()).intValue() - ((Integer)tuple.getFirst()).intValue()));
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte par1)
    {
        if (par1 == 12)
        {
            this.generateRandomParticles("heart");
        }
        else if (par1 == 13)
        {
            this.generateRandomParticles("angryVillager");
        }
        else if (par1 == 14)
        {
            this.generateRandomParticles("happyVillager");
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
        return par1EntityLivingData;
    }

    /**
     * par1 is the particleName
     */
    @SideOnly(Side.CLIENT)
    private void generateRandomParticles(String par1Str)
    {
        for (int i = 0; i < 5; ++i)
        {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(par1Str, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 1.0D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    }

    public void setLookingForHome()
    {
        this.isLookingForHome = true;
    }

    public EntitymodVillager createChild(EntityAgeable par1EntityAgeable)
    {
        EntitymodVillager entityvillager = new EntitymodVillager(this.worldObj);
        entityvillager.onSpawnWithEgg((IEntityLivingData)null);
        return entityvillager;
    }

    public boolean allowLeashing()
    {
        return false;
    }

    static
    {
        villagersSellingList.put(Items.coal, new Tuple(Integer.valueOf(16), Integer.valueOf(24)));
        villagersSellingList.put(Items.iron_ingot, new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
        villagersSellingList.put(Items.gold_ingot, new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
        villagersSellingList.put(Items.diamond, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        villagersSellingList.put(Items.paper, new Tuple(Integer.valueOf(24), Integer.valueOf(36)));
        villagersSellingList.put(Items.book, new Tuple(Integer.valueOf(11), Integer.valueOf(13)));
        villagersSellingList.put(Items.written_book, new Tuple(Integer.valueOf(1), Integer.valueOf(1)));
        villagersSellingList.put(Items.ender_pearl, new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
        villagersSellingList.put(Items.ender_eye, new Tuple(Integer.valueOf(2), Integer.valueOf(3)));
        villagersSellingList.put(Items.porkchop, new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
        villagersSellingList.put(Items.beef, new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
        villagersSellingList.put(Items.chicken, new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
        villagersSellingList.put(Items.cooked_fished, new Tuple(Integer.valueOf(9), Integer.valueOf(13)));
        villagersSellingList.put(Items.wheat_seeds, new Tuple(Integer.valueOf(34), Integer.valueOf(48)));
        villagersSellingList.put(Items.melon_seeds, new Tuple(Integer.valueOf(30), Integer.valueOf(38)));
        villagersSellingList.put(Items.pumpkin_seeds, new Tuple(Integer.valueOf(30), Integer.valueOf(38)));
        villagersSellingList.put(Items.wheat, new Tuple(Integer.valueOf(18), Integer.valueOf(22)));
        villagersSellingList.put(Item.getItemFromBlock(Blocks.wool), new Tuple(Integer.valueOf(14), Integer.valueOf(22)));
        villagersSellingList.put(Items.rotten_flesh, new Tuple(Integer.valueOf(36), Integer.valueOf(64)));
        blacksmithSellingList.put(Items.flint_and_steel, new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
        blacksmithSellingList.put(Items.shears, new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
        blacksmithSellingList.put(Items.iron_sword, new Tuple(Integer.valueOf(7), Integer.valueOf(11)));
        blacksmithSellingList.put(Items.diamond_sword, new Tuple(Integer.valueOf(12), Integer.valueOf(14)));
        blacksmithSellingList.put(Items.iron_axe, new Tuple(Integer.valueOf(6), Integer.valueOf(8)));
        blacksmithSellingList.put(Items.diamond_axe, new Tuple(Integer.valueOf(9), Integer.valueOf(12)));
        blacksmithSellingList.put(Items.iron_pickaxe, new Tuple(Integer.valueOf(7), Integer.valueOf(9)));
        blacksmithSellingList.put(Items.diamond_pickaxe, new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
        blacksmithSellingList.put(Items.iron_shovel, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        blacksmithSellingList.put(Items.diamond_shovel, new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
        blacksmithSellingList.put(Items.iron_hoe, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        blacksmithSellingList.put(Items.diamond_hoe, new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
        blacksmithSellingList.put(Items.iron_boots, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        blacksmithSellingList.put(Items.diamond_boots, new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
        blacksmithSellingList.put(Items.iron_helmet, new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        blacksmithSellingList.put(Items.diamond_helmet, new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
        blacksmithSellingList.put(Items.iron_chestplate, new Tuple(Integer.valueOf(10), Integer.valueOf(14)));
        blacksmithSellingList.put(Items.diamond_chestplate, new Tuple(Integer.valueOf(16), Integer.valueOf(19)));
        blacksmithSellingList.put(Items.iron_leggings, new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
        blacksmithSellingList.put(Items.diamond_leggings, new Tuple(Integer.valueOf(11), Integer.valueOf(14)));
        blacksmithSellingList.put(Items.chainmail_boots, new Tuple(Integer.valueOf(5), Integer.valueOf(7)));
        blacksmithSellingList.put(Items.chainmail_helmet, new Tuple(Integer.valueOf(5), Integer.valueOf(7)));
        blacksmithSellingList.put(Items.chainmail_chestplate, new Tuple(Integer.valueOf(11), Integer.valueOf(15)));
        blacksmithSellingList.put(Items.chainmail_leggings, new Tuple(Integer.valueOf(9), Integer.valueOf(11)));
        blacksmithSellingList.put(Items.bread, new Tuple(Integer.valueOf(-4), Integer.valueOf(-2)));
        blacksmithSellingList.put(Items.melon, new Tuple(Integer.valueOf(-8), Integer.valueOf(-4)));
        blacksmithSellingList.put(Items.apple, new Tuple(Integer.valueOf(-8), Integer.valueOf(-4)));
        blacksmithSellingList.put(Items.cookie, new Tuple(Integer.valueOf(-10), Integer.valueOf(-7)));
        blacksmithSellingList.put(Item.getItemFromBlock(Blocks.glass), new Tuple(Integer.valueOf(-5), Integer.valueOf(-3)));
        blacksmithSellingList.put(Item.getItemFromBlock(Blocks.bookshelf), new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
        blacksmithSellingList.put(Items.leather_chestplate, new Tuple(Integer.valueOf(4), Integer.valueOf(5)));
        blacksmithSellingList.put(Items.leather_boots, new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
        blacksmithSellingList.put(Items.leather_helmet, new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
        blacksmithSellingList.put(Items.leather_leggings, new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
        blacksmithSellingList.put(Items.saddle, new Tuple(Integer.valueOf(6), Integer.valueOf(8)));
        blacksmithSellingList.put(Items.experience_bottle, new Tuple(Integer.valueOf(-4), Integer.valueOf(-1)));
        blacksmithSellingList.put(Items.redstone, new Tuple(Integer.valueOf(-4), Integer.valueOf(-1)));
        blacksmithSellingList.put(Items.compass, new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
        blacksmithSellingList.put(Items.clock, new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
        blacksmithSellingList.put(Item.getItemFromBlock(Blocks.glowstone), new Tuple(Integer.valueOf(-3), Integer.valueOf(-1)));
        blacksmithSellingList.put(Items.cooked_porkchop, new Tuple(Integer.valueOf(-7), Integer.valueOf(-5)));
        blacksmithSellingList.put(Items.cooked_beef, new Tuple(Integer.valueOf(-7), Integer.valueOf(-5)));
        blacksmithSellingList.put(Items.cooked_chicken, new Tuple(Integer.valueOf(-8), Integer.valueOf(-6)));
        blacksmithSellingList.put(Items.ender_eye, new Tuple(Integer.valueOf(7), Integer.valueOf(11)));
        blacksmithSellingList.put(Items.arrow, new Tuple(Integer.valueOf(-12), Integer.valueOf(-8)));
    }
}
