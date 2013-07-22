package samoht2401.universalwire.blocks;

import java.util.Random;

import samoht2401.universalwire.UniversalWire;
import samoht2401.universalwire.tileentity.TileEntityRubberSmelter;
import samoht2401.universalwire.util.Constantes;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockRubberSmelter extends BlockContainer {

	Icon front;
	Icon top;
	Icon side;

	public BlockRubberSmelter(int id) {
		super(id, Material.iron);
		setHardness(2.0F);
		setResistance(5.0F);
		setUnlocalizedName("blockRubberSmelter");
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public void registerIcons(IconRegister iconRegister) {
		front = iconRegister.registerIcon("universalwire:RubberSmelter_front");
		top = iconRegister.registerIcon("universalwire:RubberSmelter_top");
		side = iconRegister.registerIcon("universalwire:RubberSmelter_side");
	}

	@Override
	public Icon getIcon(int side, int metadata) {
		if(side == Constantes.Top)
			return top;
		if(side == metadata)
			return front;
		return this.side;
	}
	
	//@Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLiving entity, ItemStack itemStack)
	{
		super.onBlockPlacedBy(w, x, y, z, entity, itemStack);
		byte metadata = 0;
        int l1 = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (l1 == 0)
        	metadata = Constantes.North;
        if (l1 == 1)
        	metadata = Constantes.East;
        if (l1 == 2)
        	metadata = Constantes.South;
        if (l1 == 3)
        	metadata = Constantes.West;
        
        w.setBlockMetadataWithNotify(x, y, z, metadata, 2);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int idk, float what, float these, float are) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
		player.openGui(UniversalWire.instance, 0, world, x, y, z);
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	private void dropItems(World world, int x, int y, int z) {
		Random rand = new Random();

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				float rx = rand.nextFloat() * 0.8F + 0.1F;
				float ry = rand.nextFloat() * 0.8F + 0.1F;
				float rz = rand.nextFloat() * 0.8F + 0.1F;

				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z
						+ rz, new ItemStack(item.itemID, item.stackSize,
						item.getItemDamage()));

				if (item.hasTagCompound()) {
					entityItem.getEntityItem().setTagCompound(
							(NBTTagCompound) item.getTagCompound().copy());
				}

				float factor = 0.05F;
				entityItem.motionX = rand.nextGaussian() * factor;
				entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
				entityItem.motionZ = rand.nextGaussian() * factor;
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityRubberSmelter();
	}
}
