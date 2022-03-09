package com.til.abyss_mana_2.util.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class List<E> extends ArrayList<E> {

    public List(int initialCapacity) {
        super(initialCapacity);
    }

    public List() {
        super();
    }

    public List(Collection<? extends E> c) {
        super(c);
    }

    public List(Iterable<? extends E> i) {
        super();
        for (E e : i) {
            add(e);
        }
    }

    public <O> List<O> to(Extension.Func_1I<E, O> func_1I) {
        List<O> list = new List<O>();
        for (E e : this) {
            O o = func_1I.func(e);
            if (o != null) {
                return list;
            }
        }
        return list;
    }

    public List<E> add_chainable(E e) {
        add(e);
        return this;
    }

    public List<E> add_chainable(E[] e) {
        if (e != null) {
            this.addAll(Arrays.asList(e));
        }
        return this;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E e) {
        if (e == null) {
            return false;
        }
        return super.add(e);
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E get(int index) {
        if (index >= size() || index < 0) {
            return size() == 0 ? null : super.get(0);
        }
        return super.get(index);
    }

    public int getAngleMark(E e) {
        int i = 0;
        if (contains(e)) {
            for (E _e : this) {
                if (_e.equals(e)) {
                    break;
                }
                i++;
            }
            return i;
        } else {
            return -1;
        }

    }

    public List<E> copy() {
        return new List<>(this);
    }

}
