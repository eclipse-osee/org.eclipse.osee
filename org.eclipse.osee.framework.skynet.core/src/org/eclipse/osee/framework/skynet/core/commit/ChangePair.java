/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.commit;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public final class ChangePair {
   private Long gammaId;
   private ModificationType modType;

   public ChangePair() {
      this(null, null);
   }

   public ChangePair(Long gammaId, ModificationType modType) {
      super();
      this.gammaId = gammaId;
      this.modType = modType;
   }

   public Long getGammaId() {
      return gammaId;
   }

   public ModificationType getModType() {
      return modType;
   }

   public void setGammaId(Long gammaId) {
      this.gammaId = gammaId;
   }

   public void setModType(ModificationType modType) {
      this.modType = modType;
   }

   public boolean isNew() {
      return getModType() == ModificationType.NEW;
   }

   public boolean isIntroduced() {
      return getModType() == ModificationType.INTRODUCED;
   }

   public boolean isDeleted() {
      return getModType().isDeleted();
   }

   public boolean exists() {
      return getModType() != null && getGammaId() != null;
   }

   public boolean sameGammaAs(ChangePair other) {
      boolean result = false;
      if (this.getGammaId() == other.getGammaId()) {
         result = true;
      } else if (this.getGammaId() != null) {
         result = this.getGammaId().equals(other.getGammaId());
      }
      return result;
   }

   @Override
   public String toString() {
      return String.format("[%s,%s]", getGammaId(), getModType());
   }

}
