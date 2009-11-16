/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.internal.data;

import java.util.Date;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.IOseeTypeFactory;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypeFactory implements IOseeTypeFactory {

   @Override
   public TransactionRecord createTransactionRecord(int transactionNumber, Branch branch, String comment, Date time, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException {
      return new TransactionRecord(transactionNumber, branch, comment, time, authorArtId, commitArtId, txType);
   }

   @Override
   public Branch createBranch(String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) throws OseeCoreException {
      checkName(name);
      if (branchType == null) {
         throw new OseeArgumentException("branchType cannot be null.");
      }
      if (branchState == null) {
         throw new OseeArgumentException("branchState cannot be null.");
      }
      return new BranchImpl(createGuidIfNeeded(guid), name, branchType, branchState, isArchived);
   }

   private void checkName(String typeName) throws OseeCoreException {
      if (!Strings.isValid(typeName)) {
         throw new OseeArgumentException("name cannot be null.");
      }
   }

   private String createGuidIfNeeded(String guid) {
      return guid == null ? GUID.create() : guid;
   }

}
