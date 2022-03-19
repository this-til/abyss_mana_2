package com.til.abyss_mana_2.util.extension;

import com.til.abyss_mana_2.common.register.ManaLevel;
import com.til.abyss_mana_2.common.register.Ore;
import com.til.abyss_mana_2.common.register.ShapedDrive;
import com.til.abyss_mana_2.common.register.ShapedType;

import javax.annotation.Nullable;
import java.awt.*;

public class GenericParadigmMap extends Map<GenericParadigmMap.IKey<?>, Object> {

    public GenericParadigmMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public GenericParadigmMap(int initialCapacity) {
        super(initialCapacity);
    }

    public GenericParadigmMap() {
        super();
    }

    public <V> V get(IKey<V> key) {
        return key.get(this);
    }

    public <V> V get(IKey<V> iKey, Extension.Func<V> func) {
        V v = get(iKey);
        if (v == null) {
            V _v = func.func();
            if (_v != null) {
                v = _v;
                put(iKey, _v);
            }
        }
        return v;
    }

    public <V> GenericParadigmMap put_genericParadigm(IKey<V> iKey, V o) {
        put(iKey, o);
        return this;
    }

    public GenericParadigmMap copy() {
        GenericParadigmMap genericParadigmMap = new GenericParadigmMap(this.size());
        genericParadigmMap.putAll(this);
        return genericParadigmMap;
    }

    public interface IKey<V> {

        @Nullable
        V get(GenericParadigmMap genericParadigmMap);

        default V _default() {
            return null;
        }

        class KeyManaLevelPack implements IKey<KeyManaLevelPack.Pack> {

            public static final KeyManaLevelPack _default = new KeyManaLevelPack();

            public interface Pack extends Extension.Func<ManaLevel> {
            }

            @Override
            public Pack get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Pack) {
                    return (Pack) o;
                }
                return _default();
            }

            @Override
            public Pack _default() {
                return () -> null;
            }
        }

        class KeyManaLevel implements IKey<ManaLevel> {

            public static final KeyManaLevel _default = new KeyManaLevel();

            @Override
            public ManaLevel get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof ManaLevel) {
                    return (ManaLevel) o;
                }
                return _default();
            }
        }

        class KeyInt implements IKey<Integer> {

            public static final KeyInt _default = new KeyInt();

            @Override
            public Integer get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Number) {
                    return (int) o;
                }
                return _default();
            }

            @Override
            public Integer _default() {
                return 0;
            }
        }

        class KeyLang implements IKey<Long> {

            public static final KeyLang _default = new KeyLang();

            @Override
            public Long get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Number) {
                    return (long) o;
                }
                return _default();
            }

            @Override
            public Long _default() {
                return 0L;
            }
        }

        class KeyColor implements IKey<Color> {

            public static final KeyColor _default = new KeyColor();

            @Override
            public Color get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Color) {
                    return (Color) o;
                }
                return _default();
            }

            @Override
            public Color _default() {
                return new Color(255, 255, 255);
            }
        }

        class KeyColorPack implements IKey<KeyColorPack.Pack> {

            @Override
            public Pack get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Pack) {
                    return (Pack) o;
                }
                return _default();
            }

            public interface Pack extends Extension.Func<Color> {

            }

            @Override
            public Pack _default() {
                return () -> new Color(255, 255, 255);
            }
        }

        class KeyOreList implements IKey<Ore[]> {

            public static final KeyOreList _default = new KeyOreList();

            protected static final List<Object> deleteList = new List<>();

            @Override
            public Ore[] get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Ore[]) {
                    return (Ore[]) o;
                }
                return _default();
            }

            @Override
            public Ore[] _default() {
                return new Ore[0];
            }
        }

        class KeyOreListPack implements IKey<KeyOreListPack.Pack> {

            public static final KeyOreListPack _default = new KeyOreListPack();

            @Override
            public Pack get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Pack) {
                    return (Pack) o;
                }
                return _default();
            }

            @Override
            public Pack _default() {
                return () -> new Ore[0];
            }

            public interface Pack extends Extension.Func<Ore[]> {
            }

        }

        class KeyString implements IKey<String> {
            public static final KeyString _default = new KeyString();

            @Override
            public String get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof String) {
                    return (String) o;
                }
                return _default();
            }

            @Override
            public String _default() {
                return "";
            }
        }

        class KeyClass implements IKey<Class<?>> {

            @Override
            public Class<?> get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Class<?>) {
                    return (Class<?>) o;
                }
                return _default();
            }

            @Override
            public Class<?> _default() {
                return Class.class;
            }
        }

        class KeyShapedDrivePack implements IKey<KeyShapedDrivePack.Pack> {

            @Override
            public Pack get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Pack) {
                    return (Pack) o;
                }
                return _default();
            }

            @Override
            public Pack _default() {
                return () -> null;
            }

            public interface Pack extends Extension.Func<ShapedDrive> {
            }

        }

        class KeyShapedTypePack implements IKey<KeyShapedTypePack.Pack> {

            @Override
            public Pack get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Pack) {
                    return (Pack) o;
                }
                return _default();
            }

            @Override
            public Pack _default() {
                return () -> null;
            }

            public interface Pack extends Extension.Func<ShapedType> {
            }

        }

        class KeyMapStingInt implements IKey<KeyMapStingInt.Pack> {

            @Override
            public Pack get(GenericParadigmMap genericParadigmMap) {
                Object o = genericParadigmMap.get((Object) this);
                if (o instanceof Pack) {
                    return (Pack) o;
                }
                return _default();
            }

            @Override
            public Pack _default() {
                return Map::new;
            }

            public interface Pack extends Extension.Func<Map<String, Integer>> {
            }

        }

    }

}
