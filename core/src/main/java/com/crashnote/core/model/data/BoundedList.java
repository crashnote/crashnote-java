/*
 *  Copyright 2010 Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.crashnote.core.model.data;

import java.util.*;

/**
 * A {@link java.util.LinkedList} version with a maximum number of elements. When adding
 * elements to the end of the list, first elements in the list are discarded if
 * the maximum size is reached.
 *
 * @param <E>
 * @author Kevin Gaudin
 */
@SuppressWarnings("serial")
public class BoundedList<E>
    extends LinkedList<E> {

    private final int maxSize;

    public BoundedList(final int maxSize) {
        this.maxSize = maxSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.LinkedList#add(java.lang.Object)
     */
    @Override
    public boolean add(final E object) {
        if (size() == maxSize)
            removeFirst();
        return super.add(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.LinkedList#add(int, java.lang.Object)
     */
    @Override
    public void add(final int location, final E object) {
        if (size() == maxSize)
            removeFirst();
        super.add(location, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.LinkedList#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.LinkedList#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(final int location, final Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.LinkedList#addFirst(java.lang.Object)
     */
    @Override
    public void addFirst(final E object) {
        // super.addFirst(object);
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.LinkedList#addLast(java.lang.Object)
     */
    @Override
    public void addLast(final E object) {
        add(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#toString()
     */
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("[ ");
        for (final E object : this) {
            result.append(object.toString());
            result.append(" ");
        }
        result.append("]");
        return result.toString();
    }
}
