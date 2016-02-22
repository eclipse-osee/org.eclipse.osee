/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributostmt:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
