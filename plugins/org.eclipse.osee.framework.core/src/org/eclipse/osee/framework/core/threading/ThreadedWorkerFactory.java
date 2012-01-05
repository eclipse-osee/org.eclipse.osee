package org.eclipse.osee.framework.core.threading;

import java.util.concurrent.Callable;

public interface ThreadedWorkerFactory<T> {

   public int getWorkSize();

   public Callable<T> createWorker(int startIndex, int endIndex);

}