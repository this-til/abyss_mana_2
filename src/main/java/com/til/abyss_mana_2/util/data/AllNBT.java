package com.til.abyss_mana_2.util.data;

import com.til.abyss_mana_2.AbyssMana2;
import com.til.abyss_mana_2.common.register.BindType;
import com.til.abyss_mana_2.util.data.message.IModDataMessage;
import com.til.abyss_mana_2.util.data.message.key_message.KeyMessage;
import com.til.abyss_mana_2.util.data.message.particle_message.ParticleMessage;
import com.til.abyss_mana_2.util.data.message.player_message.PlayerMessage;
import com.til.abyss_mana_2.util.extension.List;
import com.til.abyss_mana_2.util.extension.Map;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.Objects;


public class AllNBT {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AbyssMana2.MODID);

    public static final List<IGS<?>> ALL_NBT = new List<>();
    public static final List<IGS.IGSHashMap<?, ?>> ALL_NBT_HASH_MAP = new List<>();

    public static final Map<IModDataMessage<?>, Object> MOD_DATA_GET = new Map<>();
    public static final Map<Integer, IModDataMessage<?>> MOD_DATA = new Map<>();
    public static final List<IModDataMessage<?>> MOD_DATA_MESSAGE_LIST = new List<>();

    public static final IModDataMessage<String> particleMessage = new ParticleMessage();
    public static final IModDataMessage<PlayerMessage.MessageData> playerMessage = new PlayerMessage();
    public static final IModDataMessage<String> keyMessage = new KeyMessage();

    public static final IGS<Integer> modMana = new IGS.IntNBT("modMana");

    public static final IGS<NBTTagList> listBlockPosNBTLong = new IGS.ListNBT("listBlockPosNBTLong");
    public static final IGS<List<BlockPos>> blockPos = new IGS.ListBlockPosNBT("blockPos");

    public static final IGS<NBTBase> iManaHandleNBT = new IGS.NBTNBT("iManaHandleNBT");
    public static final IGS<NBTBase> iControlNBT = new IGS.NBTNBT("iControlNBT");
    public static final IGS<NBTBase> iHandleNBT = new IGS.NBTNBT("iHandleNBT");

    public static final IGS<Map<BindType, List<BlockPos>>> controlBindBlock = new IGS.BasicsNBT<Map<BindType, List<BlockPos>>>("controlBindBlock") {
        @Override
        public Map<BindType, List<BlockPos>> get(NBTTagCompound nbt) {
            Map<BindType, List<BlockPos>> map = new Map<>();

            for (NBTBase nbtBase : nbt.getTagList(getName(), 10)) {
                if (nbtBase instanceof NBTTagCompound) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                    BindType k = BindType.register.getValue(new ResourceLocation(nbtTagCompound.getString("k")));
                    List<BlockPos> v = new List<>();
                    for (NBTBase pos : nbtTagCompound.getTagList("v", 10)) {
                        if (pos instanceof NBTTagCompound) {
                            NBTTagCompound _pos = (NBTTagCompound) pos;
                            v.add(new BlockPos(_pos.getInteger("x"), _pos.getInteger("y"), _pos.getInteger("z")));
                        }
                    }
                    map.put(k, v);
                }
            }

            return map;
        }

        @Override
        public void set(NBTTagCompound nbt, Map<BindType, List<BlockPos>> bundTypeListMap) {
            NBTTagList mList = new NBTTagList();
            bundTypeListMap.forEach((k, v) -> {
                NBTTagCompound kv = new NBTTagCompound();
                kv.setString("k", Objects.requireNonNull(k.getRegistryName()).toString());
                NBTTagList vList = new NBTTagList();
                v.forEach(p -> {
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    nbtTagCompound.setInteger("x", p.getX());
                    nbtTagCompound.setInteger("y", p.getY());
                    nbtTagCompound.setInteger("z", p.getZ());
                    vList.appendTag(nbtTagCompound);
                });
                kv.setTag("v", vList);
                mList.appendTag(kv);
            });
            nbt.setTag(getName(), mList);

        }
    };

    public interface IGS<V> {

        V get(NBTTagCompound nbt);

        void set(NBTTagCompound nbt, V v);

        String getName();

        boolean deathRetainData();

        abstract static class BasicsNBT<V> implements IGS<V> {

            String name;

            public BasicsNBT(String name) {
                this.name = AbyssMana2.MODID + "_" + name;
                AllNBT.ALL_NBT.add(this);
            }

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public boolean deathRetainData() {
                return true;
            }

        }

        interface IGSList<V> extends IGS<List<V>> {

            V getV(NBTTagCompound nbt, int k);

            void setV(NBTTagCompound nbt, int k, V v);

            /***
             * 返回改数据的列表长度
             */
            int getLong();

            abstract class BasicsListNBT<V> implements IGSList<V> {

                String name;

                public BasicsListNBT(String name) {
                    this.name = AbyssMana2.MODID + "_" + name;
                    AllNBT.ALL_NBT.add(this);
                }

                @Override
                public String getName() {
                    return this.name;
                }

                @Override
                public boolean deathRetainData() {
                    return true;
                }

            }

        }

        interface IGSHashMap<K, V> extends IGS<Map<K, V>> {

            V getV(NBTTagCompound nbt, K k);

            void setV(NBTTagCompound nbt, K k, V v);

            /***
             * 返回改数据的列表长度
             */
            int getLong();

            abstract class BasicsHashMapNBT<K, V> implements IGSHashMap<K, V> {

                String name;

                public BasicsHashMapNBT(String name) {
                    this.name = AbyssMana2.MODID + "_" + name;
                    AllNBT.ALL_NBT.add(this);
                }

                @Override
                public String getName() {
                    return this.name;
                }

                @Override
                public boolean deathRetainData() {
                    return true;
                }

            }

        }

        class IntNBT extends BasicsNBT<Integer> {

            public IntNBT(String name) {
                super(name);
            }

            @Override
            public Integer get(NBTTagCompound nbt) {
                return nbt.getInteger(name);
            }

            @Override
            public void set(NBTTagCompound nbt, Integer v) {
                nbt.setInteger(name, v);
            }
        }

        class StringNBT extends BasicsNBT<String> {

            public StringNBT(String name) {
                super(name);
            }

            @Override
            public String get(NBTTagCompound nbt) {
                return nbt.getString(name);
            }

            @Override
            public void set(NBTTagCompound nbt, String s) {
                if (s != null)
                    nbt.setString(name, s);
            }
        }

        class DoubleNBT extends BasicsNBT<Double> {

            public DoubleNBT(String name) {
                super(name);
            }

            @Override
            public Double get(NBTTagCompound nbt) {
                return nbt.getDouble(name);
            }

            @Override
            public void set(NBTTagCompound nbt, Double aDouble) {
                nbt.setDouble(name, aDouble);
            }
        }

        class BooleanNBT extends BasicsNBT<Boolean> {

            public BooleanNBT(String name) {
                super(name);
            }

            @Override
            public Boolean get(NBTTagCompound nbt) {
                return nbt.getBoolean(name);
            }

            @Override
            public void set(NBTTagCompound nbt, Boolean v) {
                nbt.setBoolean(name, v);
            }
        }

        class HashMapIntNBT extends IGSHashMap.BasicsHashMapNBT<Integer, Integer> {

            @Override
            public Map<Integer, Integer> get(NBTTagCompound nbt) {
                Map<Integer, Integer> hashMap = new Map<>();
                int l = this.getLong() + 1;
                for (int i = 1; i < l; i++) {
                    hashMap.put(i, nbt.getInteger(name + i));
                }
                return hashMap;
            }

            @Override
            public void set(NBTTagCompound nbt, Map<Integer, Integer> integerIntegerHashMap) {
                int i = 1;
                for (Integer integer : integerIntegerHashMap.keySet()) {
                    nbt.setInteger(name + i, integer);
                    i++;
                }
            }

            public HashMapIntNBT(String name) {
                super(name);
                AllNBT.ALL_NBT_HASH_MAP.add(this);
            }

            @Override
            public Integer getV(NBTTagCompound nbt, Integer integer) {
                return nbt.getInteger(name + integer);
            }

            @Override
            public void setV(NBTTagCompound nbt, Integer integer, Integer integer2) {
                nbt.setInteger(name + integer, integer2);
            }

            @Override
            public int getLong() {
                return 10;
            }

        }

        class ListBlockPosNBT extends BasicsNBT<List<BlockPos>> {

            public static final String X = AbyssMana2.MODID + "_x";
            public static final String Y = AbyssMana2.MODID + "_y";
            public static final String Z = AbyssMana2.MODID + "_z";

            public ListBlockPosNBT(String name) {
                super(name);
            }

            @Override
            public List<BlockPos> get(NBTTagCompound nbt) {
                List<BlockPos> blockPos = new List<>();
                NBTTagList nbtTagList = AllNBT.listBlockPosNBTLong.get(nbt);
                for (int i = 0; i < nbtTagList.tagCount(); i++) {
                    NBTTagCompound blockPosNBT = nbtTagList.getCompoundTagAt(i);
                    blockPos.add(new BlockPos(blockPosNBT.getInteger(X), blockPosNBT.getInteger(Y), blockPosNBT.getInteger(Z)));
                }
                return blockPos;
            }

            @Override
            public void set(NBTTagCompound nbt, List<BlockPos> blockPos) {
                NBTTagList nbtTagList = new NBTTagList();
                for (BlockPos blockPo : blockPos) {
                    NBTTagCompound itemNBT = new NBTTagCompound();
                    itemNBT.setInteger(X, blockPo.getX());
                    itemNBT.setInteger(Y, blockPo.getY());
                    itemNBT.setInteger(Z, blockPo.getZ());
                    nbtTagList.appendTag(itemNBT);
                }
                AllNBT.listBlockPosNBTLong.set(nbt, nbtTagList);
            }
        }

        class ListNBT extends BasicsNBT<NBTTagList> {

            public ListNBT(String name) {
                super(name);
            }

            @Override
            public NBTTagList get(NBTTagCompound nbt) {
                return nbt.getTagList(name, 10);
            }

            @Override
            public void set(NBTTagCompound nbt, NBTTagList nbtBases) {
                nbt.setTag(name, nbtBases);
            }
        }

        class NBTNBT extends BasicsNBT<NBTBase> {

            public NBTNBT(String name) {
                super(name);
            }

            @Override
            public NBTBase get(NBTTagCompound nbt) {
                return nbt.getTag(name);
            }

            @Override
            public void set(NBTTagCompound nbt, NBTBase nbtBases) {
                nbt.setTag(name, nbtBases);
            }
        }

        /*class ListItemStackNBT extends BasicsNBT<List<ItemStack>> {

            public ListItemStackNBT(String name) {
                super(name);
            }

            @Override
            public List<ItemStack> get(NBTTagCompound nbt) {
                List<ItemStack> stacks = new List<>();
                NBTTagList nbtTagList = AllNBT.listItemStackNBTLong.get(nbt);
                for (int i = 0; i < nbtTagList.tagCount(); i++) {
                    NBTTagCompound itemNBT = nbtTagList.getCompoundTagAt(i);
                    stacks.add(new ItemStack(itemNBT));
                }
                return stacks;
            }

            @Override
            public void set(NBTTagCompound nbt, List<ItemStack> itemStacks) {
                NBTTagList nbtTagList = new NBTTagList();
                for (ItemStack itemStack : itemStacks) {
                    NBTTagCompound itemNBT = new NBTTagCompound();
                    nbtTagList.appendTag(itemStack.writeToNBT(itemNBT));
                }
                AllNBT.listItemStackNBTLong.set(nbt, nbtTagList);
            }
        }*/

        class ItemNBT extends BasicsNBT<Item> {

            public ItemNBT(String name) {
                super(name);
            }

            @Override
            public Item get(NBTTagCompound nbt) {
                return nbt.hasKey(name, 8) ? Item.getByNameOrId(nbt.getString(name)) : Items.AIR;
            }

            @Override
            public void set(NBTTagCompound nbt, Item item) {
                ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(item);
                nbt.setString(name, resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
            }
        }

        /*class StringName extends BasicsNBT<List<String>> {

            public StringName(String name) {
                super(name);
            }

            @Override
            public List<String> get(NBTTagCompound nbt) {
                List<String> strings = new List<>();
                NBTTagList nbtTagList = AllNBT.stringNBTList.get(nbt);
                for (int i = 0; i < nbtTagList.tagCount(); i++) {
                    NBTTagCompound nbtl = nbtTagList.getCompoundTagAt(i);
                    strings.add(nbtl.getString(name));
                }
                return strings;
            }

            @Override
            public void set(NBTTagCompound nbt, List<String> strings) {
                NBTTagList nbtTagList = new NBTTagList();
                for (String s : strings) {
                    NBTTagCompound nbtl = new NBTTagCompound();
                    nbtl.setString(name, s);
                    nbtTagList.appendTag(nbtl);
                }
                AllNBT.stringNBTList.set(nbt, nbtTagList);
            }

        }*/

    }

}
