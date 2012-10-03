package org.eclipse.osee.coverage.internal.vcast.executor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.util.XResultData;

public abstract class AbstractVcpFileWorker<T> implements Callable<Object> {

   private final XResultData logger;
   private final IProgressMonitor monitor;
   private final AtomicInteger numberProcessed;
   private final int totalSize;

   private final List<T> toProcess;
   private final List<File> processed;
   private final Map<String, CoverageUnit> fileNumToCoverageUnit;

   public AbstractVcpFileWorker(XResultData logger, IProgressMonitor monitor, AtomicInteger numberProcessed, int totalSize, List<T> toProcess, List<File> processed, Map<String, CoverageUnit> fileNumToCoverageUnit) {
      super();
      this.logger = logger;
      this.monitor = monitor;
      this.numberProcessed = numberProcessed;
      this.totalSize = totalSize;
      this.toProcess = toProcess;
      this.processed = processed;
      this.fileNumToCoverageUnit = fileNumToCoverageUnit;
   }

   @Override
   public final Object call() throws Exception {
      for (T itemToProcess : toProcess) {
         checkForCancelled();

         StringBuilder str = new StringBuilder();
         str.append("Processing VcpResultsFile ");
         str.append(numberProcessed.getAndIncrement());
         str.append("/");
         str.append(totalSize);
         monitor.worked(1);
         monitor.subTask(str.toString());

         process(itemToProcess);
      }
      return null;
   }

   protected abstract void process(T data) throws Exception;

   protected void checkForCancelled() throws CancellationException {
      if (monitor != null && monitor.isCanceled()) {
         throw new CancellationException();
      }
   }

   protected XResultData getLogger() {
      return logger;
   }

   protected List<File> getProcessed() {
      return processed;
   }

   protected Map<String, CoverageUnit> getFileNumToCoverageUnit() {
      return fileNumToCoverageUnit;
   }
}