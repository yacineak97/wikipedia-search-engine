package com.searchengine.utils;

public class ProgressLogger {

    private final String taskName;
    private final long startTime;
    private long lastUpdateTime;
    private int lastCount;
    private String unitLabel = "Pages";

    public ProgressLogger(String taskName) {
        this.taskName = taskName;
        this.startTime = System.currentTimeMillis();
        this.lastUpdateTime = startTime;
    }

    public ProgressLogger(String taskName, String unitLabel) {
        this.taskName = taskName;
        this.unitLabel = unitLabel;
        this.startTime = System.currentTimeMillis();
        this.lastUpdateTime = startTime;
    }

    public void update(int count) {
        long now = System.currentTimeMillis();

        if (now - lastUpdateTime < 1000) {
            return;
        }

        double elapsedSec = (now - startTime) / 1000.0;
        double speed = count / Math.max(elapsedSec, 0.001);

        System.out.printf(
                "\r\033[K[%s] %s: %d | Speed: %.2f %s/s | Elapsed: %.1fs",
                taskName,
                unitLabel,
                count,
                speed,
                unitLabel,
                elapsedSec
        );

        lastUpdateTime = now;
        lastCount = count;
    }

    public void finish(int count) {
        long now = System.currentTimeMillis();
        double elapsedSec = (now - startTime) / 1000.0;

        System.out.printf(
                "\n[%s DONE] %s: %d | Avg Speed: %.2f %s/s | Total Time: %.1fs\n",
                taskName,
                unitLabel,
                count,
                count / Math.max(elapsedSec, 0.001),
                unitLabel,
                elapsedSec
        );
    }
}
