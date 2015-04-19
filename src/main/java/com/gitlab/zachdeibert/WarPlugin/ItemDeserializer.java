package com.gitlab.zachdeibert.WarPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemDeserializer extends ItemStack implements ConfigurationSerializable {
    public static void init() {
        ConfigurationSerialization.registerClass(ItemDeserializer.class);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<String, Object>();
        final int typeId = getTypeId();
        final int damage = getDurability();
        final int amount = getAmount();
        final Map<Enchantment, Integer> enchantments = getEnchantments();
        if ( typeId != Material.AIR.getId() ) {
            for ( final Material mat : Material.values() ) {
                if ( mat.getId() == getTypeId() ) {
                    data.put("type", mat.name());
                    break;
                }
            }
            if ( !data.containsKey("type") ) {
                data.put("id", typeId);
            }
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
        return data;
    }
    
    public static ItemStack deserialize(final Map<String, Object> args) {
        final ItemStack stack = new ItemDeserializer();
        if ( args.containsKey("type") ) {
            stack.setType(Material.getMaterial((String) args.get("type")));
        }
        if ( args.containsKey("id") ) {
            stack.setTypeId((int) args.get("id"));
        }
        if ( args.containsKey("damage") ) {
            stack.setDurability((short) (int) args.get("damage"));
        }
        if ( args.containsKey("amount") ) {
            stack.setAmount((int) args.get("amount"));
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
