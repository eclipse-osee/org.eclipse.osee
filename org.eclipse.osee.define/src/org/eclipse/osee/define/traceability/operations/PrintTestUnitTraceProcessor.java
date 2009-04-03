/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.TestUnit;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Roberto E. Escobar
 */
public final class PrintTestUnitTraceProcessor implements ITestUnitProcessor {
   private long startTime;
   private long startMemory;

   public PrintTestUnitTraceProcessor() {
      startTime = System.currentTimeMillis();
      startMemory = Runtime.getRuntime().totalMemory();
   }

   @Override
   public void clear() {
      startTime = 0;
      startMemory = 0;
   }

   @Override
   public void initialize(IProgressMonitor monitor) {
   }

   @Override
   public void process(IProgressMonitor monitor, TestUnit testUnit) {
      for (String traceTypes : testUnit.getTraceMarkTypes()) {
         for (String traceMark : testUnit.getTraceMarksByType(traceTypes)) {
            if (monitor.isCanceled()) break;
            System.out.println(String.format("[%s] %s -- %s --> *%s*", testUnit.getTestUnitType(), testUnit.getName(),
                  traceTypes, traceMark));
         }
      }
   }

   @Override
   public void onComplete(IProgressMonitor monitor) {
      System.out.println(String.format("Completed in: %s", Lib.getElapseString(startTime)));
      System.out.println(String.format("Memory Leaked: %s", Runtime.getRuntime().totalMemory() - startMemory));
   }
}
