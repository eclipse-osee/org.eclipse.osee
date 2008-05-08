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
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.jdk.core.util.PersistenceMemo;

/**
 * @author Robert A. Fisher
 */
public class AttributeMemo implements PersistenceMemo {
   private int attrId;
   private int gammaId;

   /**
    * @param attrId
    * @param attrTypeId
    */
   public AttributeMemo(int attrId, int gammaId) {
      this.attrId = attrId;
      this.gammaId = gammaId;
   }

   /**
    * @return Returns the attrId.
    */
   public int getAttrId() {
      return attrId;
   }

   public int getGammaId() {
      return gammaId;
   }
}
