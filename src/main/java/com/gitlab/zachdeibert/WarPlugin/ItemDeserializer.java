package com.gitlab.zachdeibert.WarPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemDeserializer extends ItemStack implements ConfigurationSerializable {
    public static void init() {
        ConfigurationSerialization.registerClass(ItemDeserializer.class);
    }
    
    private static net.minecraft.item.ItemStack getHandle(final CraftItemStack stack) throws ReflectiveOperationException {
        final Field handle = stack.getClass().getDeclaredField("handle");
        handle.setAccessible(true);
        return (net.minecraft.item.ItemStack) handle.get(stack);
    }
    
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new HashMap<String, Object>();
        final Material type = getType();
        final int damage = getDurability();
        final int amount = getAmount();
        final Map<Enchantment, Integer> enchantments = getEnchantments();
        if ( type != Material.AIR ) {
            data.put("type", type.name());
        }
        if ( damage != 0 ) {
            data.put("damage", damage);
        }
        if ( amount != 1 ) {
            data.put("amount", amount);
        }
        final List<Map<String, Object>> enchsData = new LinkedList<Map<String, Object>>();
        for ( final Enchantment enchantment : enchantments.keySet() ) {
            final Map<String, Object> enchData = new HashMap<String, Object>();
            enchsData.add(enchData);
            final int level = enchantments.get(enchantment);
            enchData.put("name", enchantment.getName());
            if ( level != 1 ) {
                enchData.put("level", level);
            }
        }
        if ( enchsData.size() > 0 ) {
            data.put("enchantments", enchsData);
        }
        final NBTTagCompound nbt = CraftItemStack.asNMSCopy(this).field_77990_d;
        if ( nbt != null ) {
            final NBTTagCompound clone = (NBTTagCompound) nbt.copy();
            clone.removeTag("ench");
            final String serializedNBT = clone.toString();
            if ( !serializedNBT.equals("{}") ) {
                data.put("nbt", serializedNBT);
            }
        }
        return data;
    }
    
    public static ItemStack deserialize(final Map<String, Object> args) {
        final CraftItemStack stack = CraftItemStack.asCraftCopy(new ItemDeserializer());
        if ( args.containsKey("type") ) {
            final String type = (String) args.get("type");
            final Material mat = Material.getMaterial(type);
            if ( mat == null ) {
                throw new IllegalArgumentException(String.format("Invalid material \"%s\"", type));
            }
            stack.setType(mat);
        }
        if ( args.containsKey("damage") ) {
            stack.setDurability((short) (int) args.get("damage"));
        }
        if ( args.containsKey("amount") ) {
            stack.setAmount((int) args.get("amount"));
        }
        if ( args.containsKey("nbt") ) {
            try {
                getHandle(stack).field_77990_d = (NBTTagCompound) JsonToNBT.func_150315_a((String) args.get("nbt"));
            } catch ( final ReflectiveOperationException ex ) {
                ex.printStackTrace();
            }
        }
        if ( args.containsKey("enchantments") ) {
            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> enchantments = (List<Map<String, Object>>) args.get("enchantments");
            for ( final Map<String, Object> enchantment : enchantments ) {
                Enchantment ench = null;
                if ( enchantment.containsKey("name") ) {
                    final String name = (String) enchantment.get("name");
                    if ( name != null ) {
                        final Enchantment e = Enchantment.getByName(name);
                        if ( e != null ) {
                            ench = e;
                        }
                    }
                }
                if ( enchantment.containsKey("id") ) {
                    final Integer id = (Integer) enchantment.get("id");
                    if ( id != null ) {
                        @SuppressWarnings("deprecation")
                        final Enchantment e = Enchantment.getById(id);
                        if ( e != null ) {
                            ench = e;
                        }
                    }
                }
                if ( ench != null ) {
                    int level = 1;
                    if ( enchantment.containsKey("level") ) {
                        level = (int) enchantment.get("level");
                    }
                    stack.addUnsafeEnchantment(ench, level);
                }
            }
        }
        return stack;
    }
    
    private ItemDeserializer() {
        super(Material.AIR);
    }
}
