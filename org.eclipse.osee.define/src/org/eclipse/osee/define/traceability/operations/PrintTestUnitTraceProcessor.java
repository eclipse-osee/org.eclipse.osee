/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.TestUnit;
import org.eclipse.osee.define.traceability.ITraceParser.TraceMark;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Roberto E. Escobar
 */
public final class PrintTestUnitTraceProcessor implements ITestUnitProcessor {
   private long startTime;
   private long startMemory;
   private XResultData resultData;

   public PrintTestUnitTraceProcessor(XResultData resultData) {
      this.resultData = resultData;
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
      resultData.addRaw(AHTML.beginMultiColumnTable(95, 1));
      resultData.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Test Unit Type", "Test Unit Name",
            "Trace Type", "Trace Mark"}));
   }

   @Override
   public void process(IProgressMonitor monitor, TestUnit testUnit) {
      if (testUnit != null) {
         for (String traceTypes : testUnit.getTraceMarkTypes()) {
            for (TraceMark traceMark : testUnit.getTraceMarksByType(traceTypes)) {
               if (monitor.isCanceled()) break;
               resultData.addRaw(AHTML.addRowMultiColumnTable(testUnit.getTestUnitType(), testUnit.getName(),
                     traceMark.getTraceType(), traceMark.getRawTraceMark()));
            }
         }
      }
   }

   @Override
   public void onComplete(IProgressMonitor monitor) {
      resultData.addRaw(AHTML.endMultiColumnTable());
      System.out.println(String.format("Completed in: %s", Lib.getElapseString(startTime)));
      System.out.println(String.format("Memory Leaked: %s", Runtime.getRuntime().totalMemory() - startMemory));
   }
}
