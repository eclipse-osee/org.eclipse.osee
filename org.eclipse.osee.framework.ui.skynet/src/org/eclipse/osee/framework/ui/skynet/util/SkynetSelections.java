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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Iterator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.swt.ITreeNode;

/**
 * @author Robert A. Fisher
 */
public final class SkynetSelections {

   /*
    * This is a utility class and should not be instantiated
    */
   private SkynetSelections() {
   }

   public static boolean oneBranchSelected(IStructuredSelection selection) {
      return selection.size() == 1 && boilDownObject(selection.getFirstElement()) instanceof Branch;
   }

   public static boolean oneDescendantBranchSelected(IStructuredSelection selection) throws OseeCoreException {
      Object object = boilDownObject(selection.getFirstElement());
      return selection.size() == 1 && object instanceof Branch && ((Branch) object).hasParentBranch();
   }

   public static boolean oneTransactionSelected(IStructuredSelection selection) {
      return selection.size() == 1 && boilDownObject(selection.getFirstElement()) instanceof TransactionId;
   }

   public static boolean transactionsSelected(IStructuredSelection selection) {
      if (!selection.isEmpty()) {
         Iterator<?> iter = selection.iterator();
         while (iter.hasNext()) {
            Object object = iter.next();
            if (!(boilDownObject(object) instanceof TransactionData)) {
               return false;
            }
            return true;
         }
      }
      return false;
   }

   public static boolean twoTransactionsSelectedOnSameBranch(IStructuredSelection selection) throws OseeCoreException {
      if (selection.size() == 2) {
         Iterator<?> iter = selection.iterator();
         Object obj1 = boilDownObject(iter.next());
         Object obj2 = boilDownObject(iter.next());

         return obj1 instanceof TransactionData && obj2 instanceof TransactionData && ((TransactionData) obj1).getTransactionId().getBranch() == ((TransactionData) obj2).getTransactionId().getBranch();
      }

      return false;
   }

   public static Object boilDownObject(Object object) {
      while (object instanceof ITreeNode) {
         object = ((ITreeNode) object).getBackingData();
      }

      return object;
   }
}
