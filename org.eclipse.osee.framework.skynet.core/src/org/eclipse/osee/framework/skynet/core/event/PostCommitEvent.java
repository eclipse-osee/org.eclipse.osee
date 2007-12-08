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
public class PostCommitEvent extends Event {
   private final int transactionId;
   private final int branchId;
   private boolean archiveBranch;

   /**
    * @param sender
    */
   public PostCommitEvent(Object sender, int transactionId, int branchId, Exception exception, boolean archiveBranch) {
      super(sender, exception);

      this.transactionId = transactionId;
      this.branchId = branchId;
      this.archiveBranch = archiveBranch;
   }

   /**
    * @return Returns the transactionId.
    */
   public int getTransactionId() {
      return transactionId;
   }

   /**
    * @return Returns the branchId.
    */
   public int getBranchId() {
      return branchId;
   }

   /**
    * @return the archiveBranch
    */
   public boolean archiveBranch() {
      return archiveBranch;
   }

}