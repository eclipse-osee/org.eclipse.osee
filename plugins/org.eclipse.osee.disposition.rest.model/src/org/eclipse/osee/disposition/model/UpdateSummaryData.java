/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
