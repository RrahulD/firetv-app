/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.types;

import java.io.Serializable;

/**
 * Container to ease passing around a triplet of three objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 * <p>
 * Basically a three-variable-version of android.util.Pair.
 */
public class Triplet<T, U, V> implements Serializable {
    private static final long serialVersionUID = 9132146797132687885L;

    public final T first;
    public final U second;
    public final V third;

    public Triplet(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        result = prime * result + ((third == null) ? 0 : third.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Triplet other = (Triplet) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        if (third == null) {
            if (other.third != null)
                return false;
        } else if (!third.equals(other.third))
            return false;
        return true;
    }

    /**
     * Convenience method for creating an appropriately typed triplet.
     *
     * @param a   the first object in the Triplet
     * @param b   the second object in the Triplet
     * @param c   the third object in the Triplet
     * @param <A> the type of a
     * @param <B> the type of b
     * @param <C> the type of c
     * @return a Triplet that is templatized with the types of a, b and c
     */
    public static <A, B, C> Triplet<A, B, C> create(A a, B b, C c) {
        return new Triplet<A, B, C>(a, b, c);
    }
}
