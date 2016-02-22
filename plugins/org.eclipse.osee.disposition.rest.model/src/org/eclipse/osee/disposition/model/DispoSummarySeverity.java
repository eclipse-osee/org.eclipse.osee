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
public enum DispoSummarySeverity {
   IGNORE("Ignore"),
   WARNING("Warning"),
   ERROR("Error"),
   UPDATE("Update"),
   NEW("New");

   private String name;

   DispoSummarySeverity() {

   }

   DispoSummarySeverity(String name) {
      this.name = name;
   }

   public void setName(String name) {
      this.name = name;
   }

   //   @JsonValue
   public String getName() {
      return name;
   }

}