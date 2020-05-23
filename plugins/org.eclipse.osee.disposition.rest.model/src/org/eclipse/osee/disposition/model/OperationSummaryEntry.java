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

/**
 * @author Angel Avila
 */
public class OperationSummaryEntry {

   private String name;
   private String message;
   private DispoSummarySeverity severity;

   public OperationSummaryEntry() {

   }

   public String getName() {
      return name;
   }

   public String getMessage() {
      return message;
   }

   public DispoSummarySeverity getSeverity() {
      return severity;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public void setSeverity(DispoSummarySeverity severity) {
      this.severity = severity;
   }

}
