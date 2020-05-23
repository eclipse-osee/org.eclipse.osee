/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.importing;

/**
 * @author Robert A. Fisher
 */
public class RoughRelation {
   private final String relTypeName;
   private final String aGuid;
   private final String bGuid;
   private final String rationale;

   public RoughRelation(String relTypeName, String aGuid, String bGuid, String rationale) {
      this.relTypeName = relTypeName;
      this.aGuid = aGuid;
      this.bGuid = bGuid;
      this.rationale = rationale;
   }

   /**
    * @return Returns the relTypeName.
    */
   public String getRelationTypeName() {
      return relTypeName;
   }

   /**
    * @return the aGuid
    */
   public String getAartifactGuid() {
      return aGuid;
   }

   /**
    * @return the bGuid
    */
   public String getBartifactGuid() {
      return bGuid;
   }

   /**
    * @return the rationale
    */
   public String getRationale() {
      return rationale;
   }

}