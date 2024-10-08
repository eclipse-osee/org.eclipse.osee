/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.ide.traceability.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.ide.traceability.data.TraceMark;
import org.eclipse.osee.define.ide.traceability.data.TraceUnit;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Roberto E. Escobar
 */
public final class TraceUnitReportProcessor implements ITraceUnitProcessor {
   private long startTime;
   private long startMemory;
   private final XResultData resultData;

   public TraceUnitReportProcessor() {
      this.resultData = new XResultData();
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
      resultData.addRaw(AHTML.addHeaderRowMultiColumnTable(
         new String[] {"Test Unit Type", "Test Unit Name", "Trace Type", "Trace Mark"}));
   }

   @Override
   public void process(IProgressMonitor monitor, TraceUnit testUnit) {
      if (testUnit != null) {
         for (String traceTypes : testUnit.getTraceMarkTypes()) {
            for (TraceMark traceMark : testUnit.getTraceMarksByType(traceTypes)) {
               if (monitor.isCanceled()) {
                  break;
               }
               resultData.addRaw(AHTML.addRowMultiColumnTable(testUnit.getTraceUnitType().getName(), testUnit.getName(),
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
      XResultDataUI.report(resultData, "Report");
   }
}
