/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg.adapter;

import java.util.Iterator;
import java.util.TreeMap;

public class EpgItemProgramIterator implements Iterator<EpgItemProgram> {
    private TreeMap<Integer, EpgItemProgram> treeMap;
    private int endX;
    private Integer nextKey;

    public EpgItemProgramIterator init(TreeMap<Integer, EpgItemProgram> treeMap, int from, int to, boolean inclusive) {
        this.treeMap = treeMap;

        if (treeMap == null || treeMap.isEmpty()) {
            nextKey = null;
        } else {
            if (inclusive) {
                nextKey = treeMap.floorKey(from);
            }
            if (nextKey == null) {
                nextKey = treeMap.ceilingKey(from);
            }
        }

        this.endX = to;
        return this;
    }

    @Override
    public boolean hasNext() {
        return nextKey != null && nextKey < endX;
    }

    @Override
    public EpgItemProgram next() {
        if (!hasNext()) {
            return null;
        }

        EpgItemProgram epgItemProgram = treeMap.get(nextKey);
        nextKey = treeMap.higherKey(nextKey);
        return epgItemProgram;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
