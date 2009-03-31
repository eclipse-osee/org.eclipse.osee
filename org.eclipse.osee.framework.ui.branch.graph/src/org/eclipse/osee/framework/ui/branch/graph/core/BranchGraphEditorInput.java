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
package org.eclipse.osee.framework.ui.branch.graph.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class BranchGraphEditorInput implements IEditorInput {

   private Branch branch;
   private TransactionId transactionId;

   public BranchGraphEditorInput(Branch branch) {
      this.branch = branch;
   }

   public BranchGraphEditorInput() throws OseeCoreException {
      this(BranchManager.getSystemRootBranch());
   }

   public boolean exists() {
      return false;
   }

   public ImageDescriptor getImageDescriptor() {
      return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(getName());
   }

   public String getName() {
      return branch != null ? branch.getBranchName() : "Branch was Null";
   }

   public IPersistableElement getPersistable() {
      return null;
   }

   public String getToolTipText() {
      return getName();
   }

   public void setTransactionId(TransactionId transactionId) {
      this.transactionId = transactionId;
   }

   public TransactionId getTransactionId() {
      return transactionId;
   }

   public Branch getBranch() {
      return branch;
   }

   public boolean equals(Object obj) {
      if (obj instanceof BranchGraphEditorInput) {
         BranchGraphEditorInput compareTo = (BranchGraphEditorInput) obj;
         if (branch != null && compareTo.getBranch() != null) {
            return branch.equals(compareTo.getBranch());
         }
      }
      return super.equals(obj);
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

}
