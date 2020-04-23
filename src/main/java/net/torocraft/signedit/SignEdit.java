package net.torocraft.signedit;

import java.io.File;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
    modid = SignEdit.MODID,
    version = SignEdit.VERSION,
    name = SignEdit.MODNAME,
    acceptableRemoteVersions = "*"
)
public class SignEdit {

  public static final String MODID = "signedit";
  public static final String VERSION = "1.12.2-6";
  public static final String MODNAME = "SignEdit";

  private static final Item DEFAULT_EDITOR = Items.SIGN;

  private static Item editor;

  @EventHandler
  public void preInit(FMLPreInitializationEvent e) {
    MinecraftForge.EVENT_BUS.register(this);
    editor = getItemFromName(loadEditorItemNameFromConfig());
  }

  private static Item getItemFromName(String editorItemName) {
    if (editorItemName == null) {
      return null;
    }
    Item editor = Item.REGISTRY.getObject(new ResourceLocation(editorItemName));
    if (editor == null) {
      return DEFAULT_EDITOR;
    }
    return editor;
  }

  private static String loadEditorItemNameFromConfig() {
    Configuration cfg = new Configuration(new File("config/signedit.cfg"));
    cfg.load();

    String editorItemName = cfg.get("SignEdit", "editor", "minecraft:sign", "The player must hold this item to edit signs. Enter in the format modid:itemname. Default: 'minecraft:sign'. Use '*' to always allow editing regardless of held items.").getString();
    if (editorItemName.equals("*")) {
      return null;
    }

    cfg.save();
    return editorItemName;
  }

  @SubscribeEvent
  public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
    if (event.getWorld().isRemote){
      return;
    }

    TileEntity te = event.getEntityPlayer().world.getTileEntity(event.getPos());
    if (te != null && te instanceof TileEntitySign
        && event.getEntityPlayer().isSneaking()
        && event.getEntityPlayer().getHeldItemMainhand() != ItemStack.EMPTY
        && event.getEntityPlayer().getHeldItemMainhand().getItem().equals(editor)) {
      event.getEntityPlayer().openEditSign((TileEntitySign) te);
      event.setCanceled(true);
    }
  }
}
