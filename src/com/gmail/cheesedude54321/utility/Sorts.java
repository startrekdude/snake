/*
 * Date: 11/13/2018
 * Dev: Sam Haskins
 * Version: v2-CONTACTMANAGERV4
 */

package com.gmail.cheesedude54321.utility;

import java.util.Comparator;
import java.util.List;

/*
 * Name: Sam Haskins
 * Date: 10/7/2018
 * Inputs: Various data structures to sort
 * Outputs: Sorts those data structures
 * Description:
 *     This class implements various sorting algorithms and other useful functions
 *     related to sorting. It currently contains:
 *         - A bubble sort
 */
public final class Sorts {
    public static final int FORWARDS = 1;
    public static final int BACKWARDS = -1;
    
    /*
     * Name: Sam Haskins
     * Date: 11/12/2018
     * Generics: T, no special requirements but accepts a Comparator<T> as an argument
     * Inputs: A List<T> to sort, a Comparator<T> to compare with, and an int order
     * Outputs: Sorts the list
     * Description:
     *     Validates the order parameter to be FORWARDS or BACKWARDS, then
     *     invokes the real bubble sort over 0 to items.size - 1
     */
    public static <T> void quick(List<T> items, Comparator<T> comparator,
                                 int order) {
        // Ensure order is valid
        if (order != FORWARDS && order != BACKWARDS) {
            throw new IllegalArgumentException("Order must be Sorts.FORWARDS" +
                                               " or Sorts.BACKWARDS");
        }
        
        // Call the implementation, sorting the entire list
        quick(items, comparator, order, 0, items.size() - 1);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 11/12/2018
     * Generics: T, no special requirements but accepts a Comparator<T> as an argument
     * Inputs: A List to sort, a Comparator to compare with, an order, and the region to sort (low to high)
     * Outputs: Sorts the region of the list by sorting around a pivot then invoking recusively on both sides
     * Description:
     *     Does not validate order, as this is private the other overload does that
     *     If low < high (the sublist we're sorting is more than one element)
     *     Swap around the pivot, finding an appropriate partition index
     *     Recursively invoke with partition index to high and low to partition index
     *     Inspired by https://www.geeksforgeeks.org/quick-sort/
     */
    private static <T> void quick(List<T> items, Comparator<T> comparator,
                                  int order, int low, int high) {
        // If required, partition and recursively call quicksort again
        if (low < high) {
            T pivot = items.get(high);
            // The partition index, used in the recursive call
            int pi = low - 1;
            
            // Find out-of-order elements between low and high and swap if required
            for (int j = low; j < high; j++) {
                if (order * comparator.compare(items.get(j), pivot) < 0) {
                    pi++;
                    
                    // Do the swap
                    T temp = items.get(pi);
                    items.set(pi, items.get(j));
                    items.set(j, temp);
                }
            }
            // Swap the partition index and the highest index
            pi++;
            T temp = items.get(pi);
            items.set(pi, items.get(high));
            items.set(high, temp);
            
            // To understand recursion, you must first understand recursion
            quick(items, comparator, order, low, pi - 1);
            quick(items, comparator, order, pi + 1, high);
        }
    }
}
