package com.shubham.learner.algorithm.sorting;

import android.app.Activity;

import com.shubham.learner.LogFragment;
import com.shubham.learner.algorithm.Algorithm;
import com.shubham.learner.visualizer.SortingVisualizer;

/**
 * Created by mayank on 2/12/16.
 */

public class QuickSort extends SortAlgorithm {

    private int[] array;

    public QuickSort(SortingVisualizer visualizer, Activity activity, LogFragment logFragment) {
        this.visualizer = visualizer;
        this.activity = activity;
        this.logFragment = logFragment;
    }

    @Override
    public void run() {
        super.run();
    }

    private void sort() {
        logArray("Original array - ", array);
        if (array == null || array.length == 0) {
            return;
        }
        int length = array.length;
        quickSort(0, length - 1);
        addLog("Array has been sorted");
        completed();
    }

    private void quickSort(int lowerIndex, int higherIndex) {

        sleepFor(1000);
        int i = lowerIndex;
        int j = higherIndex;
        int pivot = array[lowerIndex + (higherIndex - lowerIndex) / 2];
        highlightTrace(pivot);
        addLog("Pivot element is " + pivot);

        while (i <= j) {
            while (array[i] < pivot) {
                i++;
            }
            while (array[j] > pivot) {
                j--;
            }
            if (i <= j) {
                swap(i, j);
                highlightSwap(i, j);
                addLog("Swapping " + array[i] + " and " + array[j]);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        if (lowerIndex < j)
            quickSort(lowerIndex, j);
        if (i < higherIndex)
            quickSort(i, higherIndex);
    }

    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    @Override
    public void onDataRecieved(Object data) {
        super.onDataRecieved(data);
        this.array = (int[]) data;
    }

    @Override
    public void onMessageReceived(String message) {
        super.onMessageReceived(message);
        if (message.equals(Algorithm.COMMAND_START_ALGORITHM)) {
            startExecution();
            sort();
        }
    }

}
