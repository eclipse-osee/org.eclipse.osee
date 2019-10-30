/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

/**
 * WorkTypes used for Category field for ATS Config objects
 *
 * @author Donald G. Dunne
 */
public enum WorkType {

   Program("Top Level item of given type for a Program"),
   Code,
   Test,
   Test_Librarian,
   Requirements,
   ImplDetails("Impl Details"),
   Applicability,
   SW_Design,
   SW_TechAppr,
   Test_Procedures,
   SubSystems,
   Software,
   Hardware,
   Issues,
   Support,
   Integration,
   Systems,
   ICDs,
   PIDS,
   SSDD,
   Maintenance,
   All,
   Custom("Custom Work Type that doesn't match a WorkType enum"),
   None;

   private String description;

   private WorkType() {
      this("");
   }

   private WorkType(String description) {
      this.description = description;
   }

   public String getDescription() {
      return description;
   }

   public static WorkType valueOfOrNone(String workTypeStr) {
      for (WorkType type : values()) {
         if (type.name().equals(workTypeStr)) {
            return type;
         }
      }
      return WorkType.None;
   }
}
