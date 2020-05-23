/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.disposition.model;

import static org.eclipse.osee.disposition.model.DispoSetStatus.FAILED;
import static org.eclipse.osee.disposition.model.DispoSetStatus.OK;
import static org.eclipse.osee.disposition.model.DispoSetStatus.WARNINGS;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angel Avila
 */

public class OperationReport {

   private DispoSetStatus status = DispoSetStatus.NO_CHANGE;

   private List<OperationSummaryEntry> entries = new ArrayList<>();
   private String name;
   private boolean failed;
   private String value;

   public OperationReport() {
   }

   public List<OperationSummaryEntry> getEntries() {
      return entries;
   }

   public void addEntry(String name, String message, DispoSummarySeverity serverity) {
      if (serverity == DispoSummarySeverity.ERROR) {
         status = FAILED;
      } else if (serverity == DispoSummarySeverity.WARNING && status != FAILED) {
         status = WARNINGS;
      } else if (status == DispoSetStatus.NO_CHANGE) {
         if (serverity == DispoSummarySeverity.NEW || serverity == DispoSummarySeverity.UPDATE) {
            status = OK;
         }
      }

      OperationSummaryEntry entry = new OperationSummaryEntry();
      entry.setName(name);
      entry.setMessage(message);
      entry.setSeverity(serverity);

      entries.add(entry);
   }

   public DispoSetStatus getStatus() {
      return status;
   }

   public void setStatus(DispoSetStatus status) {
      this.status = status;
   }

   public void setEntries(List<OperationSummaryEntry> entries) {
      this.entries = entries;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isFailed() {
      return failed;
   }

   public void setFailed(boolean failed) {
      this.failed = failed;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
