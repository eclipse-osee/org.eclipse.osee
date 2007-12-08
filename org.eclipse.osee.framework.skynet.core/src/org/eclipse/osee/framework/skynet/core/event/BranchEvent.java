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

import org.eclipse.osee.framework.ui.plugin.event.Event;

/**
 * @author Jeff C. Phillips
 */
public class BranchEvent extends Event {

   private ModType modType;
   private int branchId;

   public enum ModType {
      Deleted, New
   };

   public BranchEvent(Object sender, int branchId, ModType modType) {
      super(sender);
      this.branchId = branchId;
      this.modType = modType;
   }

   /**
    * @return Returns the branchId.
    */
   public int getBranchId() {
      return branchId;
   }

   /**
    * @return Returns the modType.
    */
   public ModType getModType() {
      return modType;
   }
}
