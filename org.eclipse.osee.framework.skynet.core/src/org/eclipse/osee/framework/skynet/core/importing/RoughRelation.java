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