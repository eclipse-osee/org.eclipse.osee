/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.applicability;

import org.eclipse.osee.framework.core.data.GammaId;

public class FeatureSelectionWithConstraints {

   private Long e1 = Long.valueOf(-1L);
   private Long e2 = Long.valueOf(-1L);
   private GammaId gammaId = GammaId.SENTINEL;
   private String value = "";
   private boolean constrained = false;
   private String constrainedBy = "";

   public FeatureSelectionWithConstraints(Long e1, Long e2, GammaId gammaId, String value, boolean constrained, String constrainedBy) {
      this.setE1(e1);
      this.setE2(e2);
      this.setGammaId(gammaId);
      this.setValue(value);
      this.setConstrained(constrained);
      this.setConstrainedBy(constrainedBy);
   }

   /**
    * @return the e1
    */
   public Long getE1() {
      return e1;
   }

   /**
    * @param e1 the e1 to set
    */
   public void setE1(Long e1) {
      this.e1 = e1;
   }

   /**
    * @return the e2
    */
   public Long getE2() {
      return e2;
   }

   /**
    * @param e2 the e2 to set
    */
   public void setE2(Long e2) {
      this.e2 = e2;
   }

   /**
    * @return the gammaId
    */
   public GammaId getGammaId() {
      return gammaId;
   }

   /**
    * @param gammaId the gammaId to set
    */
   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   /**
    * @return the value
    */
   public String getValue() {
      return value;
   }

   /**
    * @param value the value to set
    */
   public void setValue(String value) {
      this.value = value;
   }

   /**
    * @return the constrained
    */
   public boolean isConstrained() {
      return constrained;
   }

   /**
    * @param constrained the constrained to set
    */
   public void setConstrained(boolean constrained) {
      this.constrained = constrained;
   }

   /**
    * @return the constrainedBy
    */
   public String getConstrainedBy() {
      return constrainedBy;
   }

   /**
    * @param constrainedBy the constrainedBy to set
    */
   public void setConstrainedBy(String constrainedBy) {
      this.constrainedBy = constrainedBy;
   }

}
