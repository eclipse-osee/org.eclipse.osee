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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class BranchGraphEditorInput implements IEditorInput {

   private final IOseeBranch branch;
   private TransactionRecord transactionId;

   public BranchGraphEditorInput(IOseeBranch branch) {
      this.branch = branch;
   }

   public BranchGraphEditorInput() {
      this(CoreBranches.SYSTEM_ROOT);
   }

   @Override
   public boolean exists() {
      return false;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(getName());
   }

   @Override
   public String getName() {
      return branch != null ? branch.getName() : "Branch was Null";
   }

   @Override
   public IPersistableElement getPersistable() {
      return null;
   }

   @Override
   public String getToolTipText() {
      return getName();
   }

   public void setTransactionId(TransactionRecord transactionId) {
      this.transactionId = transactionId;
   }

   public TransactionRecord getTransactionId() {
      return transactionId;
   }

   public BranchId getBranch() {
      return branch;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BranchGraphEditorInput) {
         BranchGraphEditorInput compareTo = (BranchGraphEditorInput) obj;
         if (branch != null && compareTo.getBranch() != null) {
            return branch.equals(compareTo.getBranch());
         }
      }
      return super.equals(obj);
   }

   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

}
