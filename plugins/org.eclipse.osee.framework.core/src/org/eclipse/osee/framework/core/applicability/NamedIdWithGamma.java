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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

public class NamedIdWithGamma extends NamedIdBase {
   public static NamedIdWithGamma SENTINEL = new NamedIdWithGamma(-1L, "", GammaId.SENTINEL);
   private GammaId gammaId;

   public NamedIdWithGamma(Long id, String name, GammaId gammaId) {
      super(id, name);
      this.setGammaId(gammaId);
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

   @Override
   @JsonIgnore
   public String getIdString() {
      return super.getIdString();
   }

   @Override
   @JsonIgnore
   public int getIdIntValue() {
      return super.getIdIntValue();
   }
}
