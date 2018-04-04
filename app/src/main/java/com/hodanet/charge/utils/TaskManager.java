package com.hodanet.charge.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class TaskManager {
    private static TaskManager manager;
    private final ExecutorService executor = new ThreadPoolExecutor(12, 20, 30000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());

    public static TaskManager getInstance() {
        if (manager == null) {
            manager = new TaskManager();
        }
        return manager;
    }

    public void executorNewTask(Runnable task) {
        executor.execute(task);
    }
}
