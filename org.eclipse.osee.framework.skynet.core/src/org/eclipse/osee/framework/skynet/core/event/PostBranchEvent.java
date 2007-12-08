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

import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Jeff C. Phillips
 */
public class PostBranchEvent extends GuidEvent {

   private final Branch newBranch;

   /**
    * @param sender
    */
   public PostBranchEvent(Object sender, Branch newBranch) {
      super(sender);

      this.newBranch = newBranch;
   }

   /**
    * @return Returns the transactionId.
    */
   public Branch getBranch() {
      return newBranch;
   }

}
