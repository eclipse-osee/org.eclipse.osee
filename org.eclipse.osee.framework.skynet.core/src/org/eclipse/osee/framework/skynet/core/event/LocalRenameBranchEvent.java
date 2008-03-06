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

package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public class LocalRenameBranchEvent extends LocalBranchEvent {

   private final String branchName;
   private final String shortName;

   /**
    * @param sender
    * @param branchId
    */
   public LocalRenameBranchEvent(Object sender, int branchId, String branchName, String shortName) {
      super(sender, branchId);
      this.branchName = branchName;
      this.shortName = shortName;
   }

   /**
    * @return the branchName
    */
   public String getBranchName() {
      return branchName;
   }

   /**
    * @return the shortName
    */
   public String getShortName() {
      return shortName;
   }

}
