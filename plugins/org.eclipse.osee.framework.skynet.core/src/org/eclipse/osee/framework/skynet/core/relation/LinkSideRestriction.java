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

/**
 * @author Jeff C. Phillips
 */
public class LinkSideRestriction {

   private int sideALinkMax;
   private int sideBLinkMax;

   /**
    * 
    */
   public LinkSideRestriction(int sideALinkMax, int sideBLinkMax) {
      super();

      this.sideALinkMax = sideALinkMax;
      this.sideBLinkMax = sideBLinkMax;
   }

   /**
    * @return Returns the sideALinkMax.
    */
   public int getSideALinkMax() {
      return sideALinkMax;
   }

   /**
    * @return Returns the sideBLinkMax.
    */
   public int getSideBLinkMax() {
      return sideBLinkMax;
   }

}
