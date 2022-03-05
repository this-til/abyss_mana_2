package com.til.abyss_mana_2.util.extension;

import java.util.HashMap;

public class Map<K, V> extends HashMap<K, V> {


    public Map(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public Map(int initialCapacity) {
        super(initialCapacity);
    }

    public Map() {
        super();
    }

    public Map(java.util.Map<? extends K, ? extends V> m) {
        super(m);
    }

    public V get(K k, Extension.Func<V> func) {
        if (k == null) {
            return func.func();
        }
        if (containsKey(k)) {
            V v = get(k);
            if (v == null) {
                V nv = func.func();
                if (nv != null) {
                    put(k, nv);
                }
                return nv;
            } else {
                return v;
            }
        } else {
            V nv = func.func();
            if (nv != null) {
                put(k, nv);
            }
            return nv;
        }
    }

    public K getKey(V v) {
        if (v == null) {
            return null;
        }
        for (Entry<K, V> kvEntry : entrySet()) {
            if (kvEntry.getValue().equals(v)) {
                return kvEntry.getKey();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (key != null && value != null) {
            return super.put(key, value);
        } else return null;
    }

    public void put(Extension.Data_2<K, V> data_2) {
        put(data_2.d1, data_2.d2);
    }

    public Map<K, V> put_chainable(K k, V v) {
        put(k, v);
        return this;
    }

    public <KO, VO> Map<KO, VO> to(Extension.Func_2I<K, V, Extension.Data_2<KO, VO>> func2I) {
        Map<KO, VO> d = new Map<>();
        for (Entry<K, V> kvEntry : this.entrySet()) {
            Extension.Data_2<KO, VO> data_2 = func2I.func(kvEntry.getKey(), kvEntry.getValue());
            if (data_2 != null && data_2.d1 != null && data_2.d2 != null) {
                d.put(data_2);
            }
        }
        return d;
    }

}
