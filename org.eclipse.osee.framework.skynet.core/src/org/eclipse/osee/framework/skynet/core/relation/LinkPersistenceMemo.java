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
package org.eclipse.osee.framework.skynet.core.relation;

import org.eclipse.osee.framework.jdk.core.util.PersistenceMemo;

/**
 * @author Jeff C. Phillips
 * @author Robert A. Fisher
 */
public class LinkPersistenceMemo implements PersistenceMemo {

   private int linkId;
   private int gammaId;

   protected LinkPersistenceMemo(int linkId, int gammaId) {
      super();
      this.linkId = linkId;
      this.gammaId = gammaId;
   }

   public int getLinkId() {
      return linkId;
   }

   /**
    * @param linkId The linkId to set.
    */
   public void setLinkId(int linkId) {
      this.linkId = linkId;
   }

   /**
    * @return the gammaId
    */
   public int getGammaId() {
      return gammaId;
   }

   /**
    * @param gammaId the gammaId to set
    */
   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }
}
