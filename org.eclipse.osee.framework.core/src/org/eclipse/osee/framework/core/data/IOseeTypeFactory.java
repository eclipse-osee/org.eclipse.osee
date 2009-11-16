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
package org.eclipse.osee.framework.core.data;

import java.util.Date;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeTypeFactory {

   public Branch createBranch(String guid, String name, BranchType branchType, BranchState branchState, boolean isArchived) throws OseeCoreException;

   public TransactionRecord createTransactionRecord(int transactionNumber, Branch branch, String comment, Date time, int authorArtId, int commitArtId, TransactionDetailsType txType) throws OseeCoreException;
}
