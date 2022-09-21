package com.useanvil.examples.runnable;

public interface IRunnable {

    void run(String apiKey) throws Exception;

    void run(String apiKey, String otherArg) throws Exception;
}
