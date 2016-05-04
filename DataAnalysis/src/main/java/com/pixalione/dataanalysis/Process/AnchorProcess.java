package com.pixalione.dataanalysis.Process;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Created by OUASMINE Mohammed Amine on 04/05/2016.
 */
public class AnchorProcess extends RecursiveTask<HashMap<String,Integer>> {

    private Queue<String> workLoad = new LinkedList<>();
    public static final int MAX_ELEMENTS = 10;

    public AnchorProcess(Queue<String> workLoad) {
        this.workLoad = workLoad;
    }

    @Override
    protected HashMap<String,Integer> compute() {
        //if work is above threshold, break tasks up into smaller tasks

        if (this.workLoad.size() > MAX_ELEMENTS) {
            //System.out.println("#1 contenu de Queue : " + this.workLoad);

            Queue<AnchorProcess> subtasks = new ConcurrentLinkedQueue<>();

            subtasks.addAll(createSubtasks(MAX_ELEMENTS));
            subtasks.forEach(AnchorProcess::fork);

            HashMap<String,Integer> result = new HashMap<>();
            for (AnchorProcess subtask : subtasks) {
                System.out.println("JOIN() " + subtask.join());
                result.putAll(subtask.join());
            }
            //System.out.println("RESULT : " +result);
            return result;

        } else {
            //System.out.println("#2 contenu de Queue : " + this.workLoad);
            System.out.println("Doing workLoad myself: " + this.workLoad);

            return this.countWord(this.workLoad);
        }
    }

    private Queue<AnchorProcess> createSubtasks(int MAX_ELEMENTS_BY_SPLITE) {
        Queue<AnchorProcess> subtasks = new ConcurrentLinkedQueue<>();

        int countdown = 0;
        Queue<String> subList = new ConcurrentLinkedQueue<>();
        System.out.println("WORKLOAD : " + this.workLoad);
        while(!this.workLoad.isEmpty()) {

            //System.out.println("Countdown : "+ countdown);
            if(countdown < MAX_ELEMENTS_BY_SPLITE) {
                subList.add(this.workLoad.poll());
                //System.out.println(subList);
                countdown++;
            }else{
                AnchorProcess anchorProcess = new AnchorProcess(subList);
                System.out.println(anchorProcess.workLoad);
                subtasks.add(anchorProcess);
                subList.clear();
                countdown =0;
                subList.add(this.workLoad.poll());
            }
        }
        System.out.println("subtasks.size : " + subtasks.size());
        return subtasks;
    }

    private HashMap<String,Integer> countWord(Queue<String> anchor) {
        System.out.println("Counting...");
        HashMap<String,Integer> result = new HashMap<>();
        for (String line:anchor) {
            String tab[] = line.split(" ");
            for (int i = 0; i<tab.length;i++) {
                if (tab[i].length() > 2 ) {
                    if(result.containsKey(tab[i]))
                        result.put(tab[i],result.get(tab[i]) + 1);
                    else
                        result.put(tab[i],1);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        Queue<String> list = new ConcurrentLinkedQueue<>();
        list.add("amine_1");
        list.add("amine_2");
        list.add("amine_3");
        list.add("amine_4");
        list.add("amine_5");
        list.add("amine_1");
        list.add("amine_2");
        list.add("amine_3");
        list.add("amine_4");
        list.add("amine_5");
        list.add("amine_A");
        list.add("amine_B");
        list.add("amine_C");
        list.add("amine_D");
        list.add("amine_E");
        list.add("amine_A");
        list.add("amine_B");
        list.add("amine_C");
        list.add("amine_D");
        list.add("amine_E");
        list.add("Antonia");

        AnchorProcess myRecursiveTask = new AnchorProcess(list);

        HashMap<String,Integer> mergedResult = forkJoinPool.invoke(myRecursiveTask);

        System.out.println("mergedResult = " + mergedResult);
    }
}
