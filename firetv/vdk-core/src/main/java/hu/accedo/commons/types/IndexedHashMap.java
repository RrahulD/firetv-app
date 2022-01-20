/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides a LinkedHashMap implementation, where the values are both accessible by either key, or index.
 * Both index and key mapping are direct.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class IndexedHashMap<K, V> implements Map<K, V> {
    private LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<>();

    // BiMap for storing keys and indexes
    private List<K> keysByIndex = new ArrayList<>();
    private HashMap<K, Integer> indicesByKey = new HashMap<>();

    @Override
    public void clear() {
        linkedHashMap.clear();
        keysByIndex.clear();
        indicesByKey.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return linkedHashMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return linkedHashMap.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return linkedHashMap.entrySet();
    }

    @Override
    public V get(Object key) {
        return linkedHashMap.get(key);
    }

    public int getIndex(K key) {
        return indicesByKey.get(key);
    }

    public K getKey(int index) {
        return keysByIndex.get(index);
    }

    public V getValue(int index) {
        return linkedHashMap.get(getKey(index));
    }

    @Override
    public boolean isEmpty() {
        return linkedHashMap.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return linkedHashMap.keySet();
    }

    public void set(int index, V value) {
        put(keysByIndex.get(index), value);
    }

    @Override
    public V put(K key, V value) {
        if (!linkedHashMap.containsKey(key)) {
            indicesByKey.put(key, keysByIndex.size());
            keysByIndex.add(key);
        }

        return linkedHashMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        if (linkedHashMap.containsKey(key)) {
            keysByIndex.remove(indicesByKey.get(key));
            refreshIndicesByKey();
        }
        return linkedHashMap.remove(key);
    }

    @Override
    public int size() {
        return linkedHashMap.size();
    }

    @Override
    public Collection<V> values() {
        return linkedHashMap.values();
    }

    private void refreshIndicesByKey() {
        indicesByKey.clear();
        for (int i = 0; i < keysByIndex.size(); i++) {
            indicesByKey.put(keysByIndex.get(i), i);
        }
    }
}
