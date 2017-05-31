package com.creativemd.itemphysic.physics;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.creativemd.creativecore.common.utils.sorting.SortingList;
import com.creativemd.creativecore.common.utils.stack.InfoFuel;
import com.creativemd.creativecore.common.utils.stack.InfoName;
import com.creativemd.itemphysic.ItemDummyContainer;
import com.creativemd.itemphysic.ItemTransformer;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

public class ServerPhysic {
	
	public static Random rand = new Random();
	
	public static SortingList swimmingItems = new SortingList();
	public static SortingList burningItems = new SortingList();
	public static SortingList undestroyableItems = new SortingList();
	public static SortingList ignitingItems = new SortingList();
	
	public static void loadItemList()
	{
		swimmingItems.addSortingObjects(Material.WOOD, Material.CLOTH, Material.SPONGE, Material.PACKED_ICE, Material.ICE, Material.LEAVES, Material.PLANTS, Material.CARPET,
				Material.SNOW, Material.CACTUS, Material.CAKE, Material.VINE, Material.WEB, Blocks.SNOW, new InfoName("wooden"),
				Items.APPLE, Items.BOW, Items.BOWL, Items.ARROW, Items.APPLE, Items.STRING, Items.FEATHER,
				Items.WHEAT, Items.BREAD, Items.PAINTING, Items.SIGN, Items.ACACIA_BOAT, Items.ACACIA_DOOR, Items.DARK_OAK_BOAT, Items.DARK_OAK_DOOR,
				Items.BIRCH_BOAT, Items.BIRCH_DOOR, Items.JUNGLE_BOAT, Items.JUNGLE_DOOR, Items.BOAT, Items.OAK_DOOR, Items.SPRUCE_BOAT, Items.SPRUCE_DOOR,
				Items.SADDLE, Items.BONE, Items.SUGAR, Items.EGG, Items.FISHING_ROD, Items.DYE, Items.CAKE, Items.BED, Items.MELON, Items.SHEARS,
				Items.CARROT, Items.POTATO, Items.POISONOUS_POTATO, Items.BAKED_POTATO, Items.PUMPKIN_PIE,
				Items.ELYTRA, Items.MUTTON, Items.COOKED_MUTTON, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.BEETROOT, Items.BEETROOT_SEEDS,
				Items.BEETROOT_SOUP, Items.SHIELD, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.SNOWBALL);
		
		burningItems.addSortingObjects(Material.WOOD, Material.CLOTH, Material.SPONGE, Material.PACKED_ICE, Material.ICE, Material.LEAVES, Material.PLANTS, Material.CARPET,
				Material.SNOW, Material.CACTUS, Material.CAKE, Material.VINE, Material.WEB, Material.GRASS, Blocks.SNOW, new InfoName("axe"), new InfoName("wooden"),
				new InfoName("shovel"),	new InfoName("hoe"), new InfoName("sword"), Items.APPLE, Items.BOW, Items.BOWL, Items.ARROW, Items.APPLE, Items.STRING, Items.FEATHER,
				Items.WHEAT, Items.BREAD, Items.LEATHER, Items.LEATHER_BOOTS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_LEGGINGS, Items.LEAD,
				Items.PAINTING, Items.SIGN, Items.ACACIA_BOAT, Items.ACACIA_DOOR, Items.DARK_OAK_BOAT, Items.DARK_OAK_DOOR, Items.BIRCH_BOAT, Items.BIRCH_DOOR,
				Items.JUNGLE_BOAT, Items.JUNGLE_DOOR, Items.BOAT, Items.OAK_DOOR, Items.SPRUCE_BOAT, Items.SPRUCE_DOOR, Items.SADDLE, Items.BONE, Items.SUGAR,
				Items.PAPER, Items.BOOK, Items.EGG, Items.FISHING_ROD, Items.DYE, Items.CAKE, Items.BED, Items.MELON, Items.SHEARS, Items.WRITABLE_BOOK,
				Items.WRITTEN_BOOK, Items.CARROT, Items.POTATO, Items.POISONOUS_POTATO, Items.BAKED_POTATO, Items.MAP, Items.PUMPKIN_PIE, Items.NAME_TAG, Items.ENCHANTED_BOOK,
				Items.ELYTRA, Items.MUTTON, Items.COOKED_MUTTON, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.BEETROOT, Items.BEETROOT_SEEDS,
				Items.BEETROOT_SOUP, Items.SHIELD, Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, new InfoFuel(), Items.SPIDER_EYE, Items.ROTTEN_FLESH,
				Items.SNOWBALL);
		
		undestroyableItems.addSortingObjects(Items.NETHER_STAR, Blocks.BEDROCK, Blocks.OBSIDIAN, Material.BARRIER);
		
		ignitingItems.addSortingObjects(Material.LAVA, Blocks.TORCH, Items.LAVA_BUCKET, Items.BLAZE_POWDER);
	}
	
	public static DataParameter<Optional<ItemStack>> ITEM = null;
	
	
	//replace with if (!this.func_189652_ae()) { this.motionY -= 0.03999999910593033D; } 
	public static void updatePre(EntityItem item)
	{
		ItemStack stack = item.getEntityItem();
		float f = 0.98F;
        fluid = CommonPhysic.getFluid(item);
        if(fluid == null)
        {
        	item.motionY -= 0.04D;
        }else{
        	double density = (double)fluid.getDensity()/1000D;
        	double speed = - 1/density * 0.01;
        	if(swimmingItems.canPass(stack))
            	speed = 0.05;
        	
        	double speedreduction = (speed-item.motionY)/2;
        	double maxSpeedReduction = 0.05;
        	if(speedreduction < -maxSpeedReduction)
        		speedreduction = -maxSpeedReduction;
        	if(speedreduction > maxSpeedReduction)
        		speedreduction = maxSpeedReduction;
        	item.motionY += speedreduction;
        	f = (float) (1D/density/1.2);
        }
	}
	
	//replace with: if (this.worldObj.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA) { this.motionY = 0.20000000298023224D; this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F); this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F); this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F); }
	public static void updateBurn(EntityItem item)
	{	
		if (item.worldObj.getBlockState(new BlockPos(item)).getMaterial() == Material.LAVA && burningItems.canPass(item.getEntityItem()))
        {
    		item.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + rand.nextFloat() * 0.4F);
            for(int i = 0; i < 100; i++)
            	item.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, item.posX, item.posY, item.posZ, (rand.nextFloat()*0.1)-0.05, 0.2*rand.nextDouble(), (rand.nextFloat()*0.1)-0.05);
        }
		
		if(ItemDummyContainer.enableIgniting && !item.worldObj.isRemote && item.onGround && Math.random() <= 0.1 && ignitingItems.canPass(item.getEntityItem()))
		{
			IBlockState state = item.worldObj.getBlockState(new BlockPos(item).down());
			if(state.getMaterial().getCanBurn())
				item.worldObj.setBlockState(new BlockPos(item), Blocks.FIRE.getDefaultState());
		}
	}
	
	public static Fluid fluid;
	
	//Remove this.motionY *= 0.9800000190734863D;
	//Replace with: if (this.onGround){ this.motionY *= -0.5D; }
	public static void updatePost(EntityItem item)
	{
		if(fluid == null)
        {
            item.motionY *= 0.98D;
            
            if (item.onGround)
            {
            	item.motionY *= -0.5D;
            }
        }else{
        	item.motionX /= fluid.getDensity()/950D;
        	item.motionZ /= fluid.getDensity()/950D;
        }
		
		if(item.lifespan == 6000 && item.lifespan != ItemDummyContainer.despawnItem)
			item.lifespan = ItemDummyContainer.despawnItem;
		
		
	}
	
	public static int getAge(EntityItem item)
	{
		return (Integer) ReflectionHelper.getPrivateValue(EntityItem.class, item, "age", "field_70292_b");
	}
	
	public static void updateFallState(EntityItem item, double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
        if (onGroundIn && item.fallDistance > 0.0F)
        	item.playSound(SoundEvents.BLOCK_CLOTH_FALL, Math.min(1, item.fallDistance/10), (float) Math.random()*1F+1);
    }
	
	public static boolean onCollideWithPlayer(EntityItem item, EntityPlayer par1EntityPlayer)
    {
		if(ItemDummyContainer.customPickup && !par1EntityPlayer.isSneaking())
			return true;
        if (item.worldObj.isRemote || item.cannotPickup())
            return true;
        return false;
    }
	
	public static void onCollideWithPlayer(EntityItem item, EntityPlayer player, boolean needsSneak)
    {
		if(ItemDummyContainer.customPickup && needsSneak && !player.isSneaking())
			return;
        if (!item.worldObj.isRemote)
        {
            if (!ItemDummyContainer.customPickup && item.cannotPickup())
            	return;
            ItemStack itemstack = item.getEntityItem();
            int i = itemstack.stackSize;

            int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(item, player, itemstack);
            if (hook < 0) return;

            if ((!item.cannotPickup() || ItemDummyContainer.customPickup) && (item.getOwner() == null || item.lifespan - getAge(item) <= 200 || item.getOwner().equals(player.getName())) && (hook == 1 || i <= 0 || player.inventory.addItemStackToInventory(itemstack)))
            {
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.LOG))
                {
                	player.addStat(AchievementList.MINE_WOOD);
                }

                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.LOG2))
                {
                	player.addStat(AchievementList.MINE_WOOD);
                }

                if (itemstack.getItem() == Items.LEATHER)
                {
                	player.addStat(AchievementList.KILL_COW);
                }

                if (itemstack.getItem() == Items.DIAMOND)
                {
                	player.addStat(AchievementList.DIAMONDS);
                }

                if (itemstack.getItem() == Items.BLAZE_ROD)
                {
                	player.addStat(AchievementList.BLAZE_ROD);
                }

                if (itemstack.getItem() == Items.DIAMOND && item.getThrower() != null)
                {
                    EntityPlayer entityplayer = item.worldObj.getPlayerEntityByName(item.getThrower());

                    if (entityplayer != null && entityplayer != player)
                    {
                        entityplayer.addStat(AchievementList.DIAMONDS_TO_YOU);
                    }
                }

                net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerItemPickupEvent(player, item);
                if (!item.isSilent())
                {
                	item.worldObj.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }

                player.onItemPickup(item, i);

                if (itemstack.stackSize <= 0)
                {
                    item.setDead();
                }

                player.addStat(StatList.getObjectsPickedUpStats(itemstack.getItem()), i);
            }
        }
    }
	
	public static boolean processInitialInteract(EntityItem item, EntityPlayer player, ItemStack stack, EnumHand hand)
    {
		if(ItemDummyContainer.customPickup)
		{
			onCollideWithPlayer(item, player, false);
			return true;
		}
        return false;
    }
	
	public static boolean attackEntityFrom(EntityItem item, DamageSource source, float amount)
    {
		if (item.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (item.getEntityItem() != null && undestroyableItems.canPass(item.getEntityItem()))
        {
            return false;
        }
        else
        {
        	if((source == DamageSource.lava | source == DamageSource.onFire | source == DamageSource.inFire) && !burningItems.canPass(item.getEntityItem()))return false;
        	if(source == DamageSource.cactus)return false;
        	
        	try {
				ReflectionHelper.findMethod(Entity.class, item, new String[]{"setBeenAttacked", "func_70018_K"}).invoke(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	//item.setBeenAttacked();
        	try {
	        	Field health = ReflectionHelper.findField(EntityItem.class, "health", "field_70291_e");
	        	health.setInt(item, (int)((float)health.getInt(item) - amount));
	        	
	            if (health.getInt(item) <= 0)
	            {
	            	item.setDead();
	            }
	        } catch (Exception e) {
				e.printStackTrace();
			}

            return false;
        }
    }
	
	public static boolean isItemBurning(EntityItem item)
	{
		boolean flag = item.worldObj != null && item.worldObj.isRemote;
		try{
	        if(!(!item.isImmuneToFire() && ((Integer) ReflectionHelper.getPrivateValue(Entity.class, item, "fire", "field_70151_c") > 0 || flag && (Boolean)ReflectionHelper.findMethod(Entity.class, item, new String[]{"getFlag", "func_70083_f"}, int.class).invoke(item, 0))))
	        	return false;
		}catch(Exception e){
			e.printStackTrace();
		}
        return burningItems.canPass(item.getEntityItem());
	}
}
