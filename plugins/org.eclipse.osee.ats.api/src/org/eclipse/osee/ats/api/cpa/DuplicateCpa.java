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
package org.eclipse.osee.ats.api.cpa;

/**
 * @author Donald G. Dunne
 */
public class DuplicateCpa {

   private String programId;
   private String versionId;
   private String cpaId;
   private String userId;
   private boolean completeCpa;

   public String getCpaId() {
      return cpaId;
   }

   public void setCpaId(String cpaId) {
      this.cpaId = cpaId;
   }

   public String getProgramId() {
      return programId;
   }

   public void setProgramId(String programId) {
      this.programId = programId;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public boolean isCompleteCpa() {
      return completeCpa;
   }

   public void setCompleteCpa(boolean completeCpa) {
      this.completeCpa = completeCpa;
   }

   public String getVersionId() {
      return versionId;
   }

   public void setVersionId(String versionId) {
      this.versionId = versionId;
   }

}
