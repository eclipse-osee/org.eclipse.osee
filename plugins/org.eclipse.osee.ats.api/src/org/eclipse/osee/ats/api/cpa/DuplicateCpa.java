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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class DuplicateCpa {

   private String programUuid;
   private String cpaUuid;
   private String userId;
   private boolean completeCpa;

   public String getCpaUuid() {
      return cpaUuid;
   }

   public void setCpaUuid(String cpaUuid) {
      this.cpaUuid = cpaUuid;
   }

   public String getProgramUuid() {
      return programUuid;
   }

   public void setProgramUuid(String programUuid) {
      this.programUuid = programUuid;
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

}
