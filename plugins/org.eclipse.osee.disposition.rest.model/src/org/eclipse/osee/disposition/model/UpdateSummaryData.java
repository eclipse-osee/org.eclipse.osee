/*********************************************************************
 * Copyright (c) 2017 Boeing
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
 * @author Dominic A. Guss
 */
public class UpdateSummaryData {
   private DispoSummarySeverity severity;
   private String name;
   private String message;

   public UpdateSummaryData() {
      // Do nothing
   }

   public UpdateSummaryData(DispoSummarySeverity severity, String name, String message) {
      this.severity = severity;
      this.name = name;
      this.message = message;
   }

   public DispoSummarySeverity getSeverity() {
      return severity;
   }

   public String getName() {
      return name;
   }

   public String getMessage() {
      return message;
   }
}
