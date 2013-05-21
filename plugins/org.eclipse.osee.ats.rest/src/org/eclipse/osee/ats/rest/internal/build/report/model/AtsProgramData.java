/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.build.report.model;


/**
 * @author Megumi Telles
 */
public class AtsProgramData {

   private String programName;
   private String programId;

   public AtsProgramData() {
   }

   public String getProgramName() {
      return programName;
   }

   public void setProgramName(String programName) {
      this.programName = programName;
   }

   public String getProgramId() {
      return programId;
   }

   public void setProgramId(String progrmaId) {
      this.programId = progrmaId;
   }

}
