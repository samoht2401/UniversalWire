package samoht2401.universalwire.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UniversalWireItem extends Item {

	private String iconName;

	public UniversalWireItem(int i) {
		super(i);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return getUnlocalizedName(itemstack);
	}

	@Override
	public Item setUnlocalizedName(String par1Str) {
		iconName = par1Str;
		return super.setUnlocalizedName(par1Str);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("UniversalWire:" + iconName);
	}
}
