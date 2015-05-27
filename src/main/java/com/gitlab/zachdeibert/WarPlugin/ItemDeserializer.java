package com.gitlab.zachdeibert.WarPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagByte;
import net.minecraft.server.NBTTagByteArray;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagDouble;
import net.minecraft.server.NBTTagFloat;
import net.minecraft.server.NBTTagInt;
import net.minecraft.server.NBTTagIntArray;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagLong;
import net.minecraft.server.NBTTagShort;
import net.minecraft.server.NBTTagString;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemDeserializer extends ItemStack implements ConfigurationSerializable {
    public static void init() {
        ConfigurationSerialization.registerClass(ItemDeserializer.class);
    }
    
    public static NBTTagCompound getNBT(final ItemStack stack) throws ReflectiveOperationException {
        final Class<?> cls = stack.getClass();
        if ( cls.getName().equals("org.bukkit.craftbukkit.inventory.CraftItemStack") ) {
            final Field itemF = cls.getDeclaredField("item");
            itemF.setAccessible(true);
            final Object item = itemF.get(stack);
            final Method getTag = item.getClass().getMethod("getTag");
            return (NBTTagCompound) getTag.invoke(item);
        } else {
            final Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack");
            final Constructor<?> ctor = craftItemStack.getConstructor(ItemStack.class);
            return getNBT((ItemStack) ctor.newInstance(stack));
        }
    }
    
    public static ItemStack setNBT(final ItemStack stack, final NBTTagCompound nbt) throws ReflectiveOperationException {
        final Class<?> cls = stack.getClass();
        if ( cls.getName().equals("org.bukkit.craftbukkit.inventory.CraftItemStack") ) {
            final Field itemF = cls.getDeclaredField("item");
            itemF.setAccessible(true);
            final Object item = itemF.get(stack);
            final Method setTag = item.getClass().getMethod("setTag", NBTTagCompound.class);
            setTag.invoke(item, nbt);
            return stack;
        } else {
            final Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack");
            final Constructor<?> ctor = craftItemStack.getConstructor(ItemStack.class);
            return setNBT((ItemStack) ctor.newInstance(stack), nbt);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static NBTTagCompound removeTag(final NBTTagCompound nbt, final String tag) throws ReflectiveOperationException {
        final Field field = nbt.getClass().getDeclaredField("c");
        field.setAccessible(true);
        ((Map<String, Object>) field.get(nbt)).remove(tag);
        return nbt;
    }
    
    public static void serializeNBT(final NBTBase tag, final StringBuilder builder) throws ReflectiveOperationException {
        if ( tag instanceof NBTTagByte ) {
            builder.append(((NBTTagByte) tag).f());
            builder.append("b");
        } else if ( tag instanceof NBTTagShort ) {
            builder.append(((NBTTagShort) tag).e());
            builder.append("s");
        } else if ( tag instanceof NBTTagInt ) {
            builder.append(((NBTTagInt) tag).d());
        } else if ( tag instanceof NBTTagLong ) {
            builder.append(((NBTTagLong) tag).c());
            builder.append("L");
        } else if ( tag instanceof NBTTagFloat ) {
            builder.append(((NBTTagFloat) tag).h());
            builder.append("f");
        } else if ( tag instanceof NBTTagDouble ) {
            String data = String.valueOf(((NBTTagDouble) tag).g());
            if ( !data.contains(".") ) {
                data = data.concat(".0");
            }
            builder.append(data);
        } else if ( tag instanceof NBTTagByteArray ) {
            builder.append("<");
            final byte data[] = ((NBTTagByteArray) tag).c();
            builder.append(data.length);
            builder.append("|");
            for ( int i = 0; i < data.length; i++ ) {
                if ( i > 0 ) {
                    builder.append(",");
                }
                builder.append(data[i]);
            }
            builder.append(">");
        } else if ( tag instanceof NBTTagIntArray ) {
            builder.append(">");
            final int data[] = ((NBTTagIntArray) tag).c();
            builder.append(data.length);
            builder.append("|");
            for ( int i = 0; i < data.length; i++ ) {
                if ( i > 0 ) {
                    builder.append(",");
                }
                builder.append(data[i]);
            }
            builder.append("<");
        } else if ( tag instanceof NBTTagString ) {
            builder.append("\"");
            builder.append(((NBTTagString) tag).a_().replace("\\", "\\\\").replace("\"", "\\\""));
            builder.append("\"");
        } else if ( tag instanceof NBTTagList ) {
            builder.append("[");
            final Field field = tag.getClass().getDeclaredField("b");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            final List<NBTBase> data = (List<NBTBase>) field.get(tag);
            for ( final Iterator<NBTBase> it = data.iterator(); ; ) {
                serializeNBT(it.next(), builder);
                if ( it.hasNext() ) {
                    builder.append(",");
                } else {
                    break;
                }
            }
            builder.append("]");
        } else if ( tag instanceof NBTTagCompound ) {
            builder.append("{");
            final Field field = tag.getClass().getDeclaredField("c");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            final Map<String, NBTBase> data = (Map<String, NBTBase>) field.get(tag);
            for ( final Iterator<String> it = data.keySet().iterator(); it.hasNext(); ) {
                final String key = it.next();
                builder.append(key);
                builder.append(":");
                serializeNBT(data.get(key), builder);
                if ( it.hasNext() ) {
                    builder.append(",");
                } else {
                    break;
                }
            }
            builder.append("}");
        }
    }
    
    public static String serializeNBT(final NBTBase tag) throws ReflectiveOperationException {
        final StringBuilder builder = new StringBuilder();
        serializeNBT(tag, builder);
        return builder.toString();
    }
    
    public static NBTBase deserializeNBT(final ArrayStream data) {
        String value = "";
        char c = data.readChar();
        while ( (c >= '0' && c <= '9') || c == '.' ) {
            value += c;
            c = data.readChar();
        }
        if ( !value.equals("") ) {
            switch ( c ) {
                case 'b':
                    return new NBTTagByte(Byte.parseByte(value));
                case 's':
                    return new NBTTagShort(Short.parseShort(value));
                case 'L':
                    return new NBTTagLong(Long.parseLong(value));
                case 'f':
                    return new NBTTagFloat(Float.parseFloat(value));
                default:
                    data.offset--;
                    if ( value.contains(".") ) {
                        return new NBTTagDouble(Double.parseDouble(value));
                    } else {
                        return new NBTTagInt(Integer.parseInt(value));
                    }
            }
        } else if ( c == '<' ) {
            c = data.readChar();
            while ( c != '|' ) {
                value += c;
            }
            final byte bytes[] = new byte[Integer.parseInt(value)];
            for ( int i = 0; i < bytes.length; i++ ) {
                value = "";
                c = data.readChar();
                while ( c != ',' && c != '>' ) {
                    value += c;
                    c = data.readChar();
                }
                bytes[i] = Byte.parseByte(value);
            }
            return new NBTTagByteArray( bytes);
        } else if ( c == '>' ) {
            c = data.readChar();
            while ( c != '|' ) {
                value += c;
            }
            final int ints[] = new int[Integer.parseInt(value)];
            for ( int i = 0; i < ints.length; i++ ) {
                value = "";
                c = data.readChar();
                while ( c != ',' && c != '<' ) {
                    value += c;
                    c = data.readChar();
                }
                ints[i] = Integer.parseInt(value);
            }
            return new NBTTagIntArray(ints);
        } else if ( c == '[' ) {
            final NBTTagList tag = new NBTTagList();
            data.readWhitespace();
            while ( c != ']' ) {
                tag.add(deserializeNBT(data));
                data.readWhitespace();
                c = data.readChar();
                data.readWhitespace();
            }
            return tag;
        } else if ( c == '{' ) {
            final NBTTagCompound tag = new NBTTagCompound();
            data.readWhitespace();
            while ( c != '}' ) {
                value = "";
                c = data.readChar();
                while ( c != ':' ) {
                    value += c;
                    c = data.readChar();
                }
                data.readWhitespace();
                tag.set(value, deserializeNBT(data));
                data.readWhitespace();
                c = data.readChar();
                data.readWhitespace();
            }
            return tag;
        }
        return null;
    }
    
    public static NBTBase deserializeNBT(final String data) {
        return deserializeNBT(new ArrayStream(data.toCharArray()));
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<String, Object>();
        final Material type = getType();
        final int damage = getDurability();
        final int amount = getAmount();
        final Map<Enchantment, Integer> enchantments = getEnchantments();
        if ( type != Material.AIR ) {
            data.put("name", type.name());
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
        try {
            final NBTTagCompound nbt = getNBT(this);
            if ( nbt != null ) {
                final String serializedNBT = serializeNBT(removeTag((NBTTagCompound) nbt.clone(), "ench"));
                if ( !serializedNBT.equals("{}") ) {
                    data.put("nbt", serializedNBT);
                }
            }
        } catch ( final ReflectiveOperationException ex ) {
            ex.printStackTrace();
        }
        return data;
    }
    
    public static ItemStack deserialize(final Map<String, Object> args) {
        final ItemStack stack = new ItemDeserializer();
        if ( args.containsKey("type") ) {
            stack.setType(Material.getMaterial((String) args.get("type")));
        }
        if ( args.containsKey("id") ) {
            stack.setType(Material.getMaterial((String) args.get("name")));
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
        try {
            if ( args.containsKey("nbt") ) {
                return setNBT(stack, (NBTTagCompound) deserializeNBT((String) args.get("nbt")));
            }
        } catch ( final ReflectiveOperationException ex ) {
            ex.printStackTrace();
        }
        return stack;
    }
    
    private ItemDeserializer() {
        super(Material.AIR);
    }
}
