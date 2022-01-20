/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.types;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bi-directional HashMap, where you can access both value by key, or key by value.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class BiMap<K, V> implements Map<K, V> {
    private HashMap<K, V> map = new HashMap<>();
    private HashMap<V, K> mapInverse = new HashMap<>();

    @Override
    public void clear() {
        map.clear();
        mapInverse.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(map.entrySet());
    }

    public Set<java.util.Map.Entry<V, K>> entrySetInverse() {
        return Collections.unmodifiableSet(mapInverse.entrySet());
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    public K getInverse(Object key) {
        return mapInverse.get(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public V put(K key, V value) {
        mapInverse.put(value, key);
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        V value = map.remove(key);
        mapInverse.remove(value);
        return value;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(map.keySet());
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(map.values());
    }
}
