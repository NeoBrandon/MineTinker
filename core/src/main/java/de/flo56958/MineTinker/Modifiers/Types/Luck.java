package de.flo56958.MineTinker.Modifiers.Types;

import de.flo56958.MineTinker.Data.ModifierFailCause;
import de.flo56958.MineTinker.Data.ToolType;
import de.flo56958.MineTinker.Events.ModifierFailEvent;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Modifiers.Modifier;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class Luck extends Modifier {

    private static EnumMap<ToolType, List<Enchantment>> applicableEnchants = new EnumMap<>(ToolType.class);

    private static Luck instance;

    static {
        applicableEnchants.put(ToolType.AXE, new ArrayList<Enchantment>() {{ add(Enchantment.LOOT_BONUS_BLOCKS); add(Enchantment.LOOT_BONUS_MOBS); }});
        applicableEnchants.put(ToolType.BOW, Collections.singletonList(Enchantment.LOOT_BONUS_MOBS));
        applicableEnchants.put(ToolType.HOE, Collections.singletonList(Enchantment.LOOT_BONUS_BLOCKS));
        applicableEnchants.put(ToolType.PICKAXE, Collections.singletonList(Enchantment.LOOT_BONUS_BLOCKS));
        applicableEnchants.put(ToolType.SHOVEL, Collections.singletonList(Enchantment.LOOT_BONUS_BLOCKS));
        applicableEnchants.put(ToolType.SWORD, Collections.singletonList(Enchantment.LOOT_BONUS_MOBS));
        applicableEnchants.put(ToolType.SHEARS, Collections.singletonList(Enchantment.LOOT_BONUS_BLOCKS));
        applicableEnchants.put(ToolType.FISHINGROD, Collections.singletonList(Enchantment.LUCK));
    }

    public static Luck instance() {
        synchronized (Luck.class) {
            if (instance == null) instance = new Luck();
        }

        return instance;

    }

    private Luck() {
        super("Luck", "Luck.yml",
                new ArrayList<>(Arrays.asList(ToolType.AXE, ToolType.BOW, ToolType.CROSSBOW, ToolType.HOE, ToolType.PICKAXE, ToolType.SHEARS,
                        ToolType.FISHINGROD, ToolType.SHOVEL, ToolType.SWORD, ToolType.TRIDENT)),
                Main.getPlugin());
    }

    @Override
    public List<Enchantment> getAppliedEnchantments() {
        List<Enchantment> enchantments = new ArrayList<>();
        enchantments.add(Enchantment.LOOT_BONUS_BLOCKS);
        enchantments.add(Enchantment.LOOT_BONUS_MOBS);
        enchantments.add(Enchantment.LUCK);

        return enchantments;
    }

    @Override
    public void reload() {
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	
    	String key = "Luck";
    	config.addDefault(key + ".allowed", true);
    	config.addDefault(key + ".name", key);
    	config.addDefault(key + ".name_modifier", "Compressed Lapis-Block");
        config.addDefault(key + ".modifier_item", "LAPIS_BLOCK"); //Needs to be a viable Material-Type
        config.addDefault(key + ".description", "Get more loot from enemies and blocks!");
        config.addDefault(key + ".description_modifier", "%WHITE%Modifier-Item for the Luck-Modifier");
        config.addDefault(key + ".Color", "%BLUE%");
        config.addDefault(key + ".EnchantCost", 10);
        config.addDefault(key + ".MaxLevel", 3);

    	config.addDefault(key + ".Recipe.Enabled", true);
    	config.addDefault(key + ".Recipe.Top", "LLL");
    	config.addDefault(key + ".Recipe.Middle", "LLL");
    	config.addDefault(key + ".Recipe.Bottom", "LLL");

        Map<String, String> recipeMaterials = new HashMap<>();
        recipeMaterials.put("L", "LAPIS_BLOCK");

        config.addDefault(key + ".Recipe.Materials", recipeMaterials);

    	ConfigurationManager.saveConfig(config);
        ConfigurationManager.loadConfig("Modifiers" + File.separator, getFileName());

        init(config.getString(key + ".name"),
                "[" + config.getString(key + ".name_modifier") + "] \u200B" + config.getString(key + ".description"),
                ChatWriter.getColor(config.getString(key + ".Color")),
                config.getInt(key + ".MaxLevel"),
                modManager.createModifierItem(Material.getMaterial(config.getString(key + ".modifier_item")), ChatWriter.getColor(config.getString(key + ".Color")) + config.getString(key + ".name_modifier"), ChatWriter.addColors(config.getString(key + ".description_modifier")), this));
    }

    @Override
    public boolean applyMod(Player p, ItemStack tool, boolean isCommand) {
        if (modManager.hasMod(tool, SilkTouch.instance())) {
            pluginManager.callEvent(new ModifierFailEvent(p, tool, this, ModifierFailCause.INCOMPATIBLE_MODIFIERS, isCommand));
            return false;
        }
        if (!Modifier.checkAndAdd(p, tool, this, "luck", isCommand)) {
            return false;
        }

        ItemMeta meta = tool.getItemMeta();

        if (meta != null) {
            for (Enchantment enchantment : applicableEnchants.get(ToolType.get(tool.getType()))) {
                meta.addEnchant(enchantment, modManager.getModLevel(tool, this), true);
            }

            if (Main.getPlugin().getConfig().getBoolean("HideEnchants")) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } else {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            tool.setItemMeta(meta);
        }

        return true;
    }

    @Override
    public void registerCraftingRecipe() {
        _registerCraftingRecipe(getConfig(), this, "Luck", "Modifier_Luck");
    }

    @Override
    public boolean isAllowed() {
    	return getConfig().getBoolean("Luck.allowed");
    }
}
