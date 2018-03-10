package studio.roboto.communimate.util

/**
 * Created by jordan on 16/10/2017.
 * jsf16 :: jordan
 */
interface SortedListComparator<E> : Comparator<E> {
    fun equal(obj1: E, obj2: E): Boolean
}