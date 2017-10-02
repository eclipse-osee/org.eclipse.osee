/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability.report;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractReport {
   protected static final String EMPTY_STRING = "";
   private final Set<IReportDataCollector> dataCollectors;

   public AbstractReport() {
      this.dataCollectors = new HashSet<>();
   }

   public void addReportDataCollector(IReportDataCollector collector) {
      if (collector != null) {
         dataCollectors.add(collector);
      }
   }

   public void removeReportDataCollector(IReportDataCollector collector) {
      if (collector != null) {
         dataCollectors.remove(collector);
      }
   }

   protected void notifyOnTableHeader(String... header) {
      if (header != null && header.length > 0) {
         for (IReportDataCollector collector : dataCollectors) {
            collector.addTableHeader(header);
         }
      }
   }

   protected void notifyOnRowData(Artifact rowArt, String... rowData) {
      if (rowData != null && rowData.length > 0) {
         for (IReportDataCollector collector : dataCollectors) {
            collector.addRow(rowArt, rowData);
         }
      }
   }

   protected void notifyOnEndTable() {
      for (IReportDataCollector collector : dataCollectors) {
         collector.endTable();
      }
   }

   public abstract void process(IProgressMonitor monitor);

   public void clear() {
      // do nothing
   }
}
