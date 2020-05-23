package com.simonreisinger.gesturerecognition;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

// Source: https://stackoverflow.com/questions/55636717/how-can-i-use-partition-function-of-opencv-in-java-wrapper
public class Partition {

    public static void main(String[] args) {
        List<String> list = new LinkedList<>(Arrays.asList("aa", "ab", "aa", "bb", "bb", "a", "b", "a", "b", "b"));
        List<Integer> list2 = new ArrayList<>(Arrays.asList(1, 2, 10, 11, 10, 42, 3, 2, 3));

        List<Integer> labels = new ArrayList<>();
        int classes = partition(list, labels);
        System.out.println("There are " + classes + " classes of equal strings:");
        System.out.println(labels);

        classes = partition(list2, labels);
        System.out.println("There are " + classes + " classes of equal integers:");
        System.out.println(labels);

        classes = partition(list, labels, (str1, str2) -> str1.length() == str2.length());
        System.out.println("There are " + classes + " classes of strings with same length:");
        System.out.println(labels);

        classes = partition(list2, labels, (i1, i2) -> Math.abs(i1 - i2) <= 1);
        System.out.println("There are " + classes + " classes of integers within 1 range:");
        System.out.println(labels);
    }

    /**
     * Overloaded method with default equality predicate
     *
     * @param _vec   vector of elements to be partitioned
     * @param labels output list of labels
     * @return number of classes
     */
    public static <E> int partition(final List<E> _vec, List<Integer> labels) {
        return partition(_vec, labels, E::equals);
    }

    /**
     * Port of C++ partition function
     *
     * @param _vec      list of elements to be partitioned
     * @param labels    output list of labels
     * @param predicate predicate to test whether two elements belong to the same class
     * @return number of classes
     */
    //template<typename _Tp, class _EqPredicate> int
    //partition( const std::vector<_Tp>& _vec, std::vector<int>& labels,
    //          _EqPredicate predicate=_EqPredicate())
    public static <E> int partition(final List<E> _vec, List<Integer> labels, BiPredicate<E, E> predicate) {

        int i, j, N = (int) _vec.size();

        //const _Tp* vec = &_vec[0];
        final ArrayList<E> vec = new ArrayList<>(_vec);

        final int PARENT = 0;
        final int RANK = 1;

        //std::vector<int> _nodes(N*2);
        //int (*nodes)[2] = (int(*)[2])&_nodes[0];
        int[][] nodes = new int[N * 2][2];

        // The first O(N) pass: create N single-vertex trees
        for (i = 0; i < N; i++) {
            nodes[i][PARENT] = -1;
            nodes[i][RANK] = 0;
        }

        // The main O(N^2) pass: merge connected components
        for (i = 0; i < N; i++) {
            int root = i;

            // find root
            while (nodes[root][PARENT] >= 0)
                root = nodes[root][PARENT];

            for (j = 0; j < N; j++) {
                //if( i == j || !predicate(vec[i], vec[j]))
                if (i == j || !predicate.test(vec.get(i), vec.get(j)))
                    continue;
                int root2 = j;

                while (nodes[root2][PARENT] >= 0)
                    root2 = nodes[root2][PARENT];

                if (root2 != root) {
                    // unite both trees
                    int rank = nodes[root][RANK], rank2 = nodes[root2][RANK];
                    if (rank > rank2)
                        nodes[root2][PARENT] = root;
                    else {
                        nodes[root][PARENT] = root2;
                        nodes[root2][RANK] += (rank == rank2 ? 1 : 0);
                        root = root2;
                    }
                    //CV_Assert( nodes[root][PARENT] < 0 );
                    assert (nodes[root][PARENT] < 0);

                    int k = j, parent;

                    // compress the path from node2 to root
                    while ((parent = nodes[k][PARENT]) >= 0) {
                        nodes[k][PARENT] = root;
                        k = parent;
                    }

                    // compress the path from node to root
                    k = i;
                    while ((parent = nodes[k][PARENT]) >= 0) {
                        nodes[k][PARENT] = root;
                        k = parent;
                    }
                }
            }
        }

        // Final O(N) pass: enumerate classes
        //labels.resize(N);
        Integer[] _labels = new Integer[N];
        int nclasses = 0;

        for (i = 0; i < N; i++) {
            int root = i;
            while (nodes[root][PARENT] >= 0)
                root = nodes[root][PARENT];
            // re-use the rank as the class label
            if (nodes[root][RANK] >= 0)
                nodes[root][RANK] = ~nclasses++;
            _labels[i] = ~nodes[root][RANK];
        }
        labels.clear();
        labels.addAll(Arrays.asList(_labels));
        return nclasses;
    }
}