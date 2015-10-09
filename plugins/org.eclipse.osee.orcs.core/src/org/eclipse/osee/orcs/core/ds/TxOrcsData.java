/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import java.util.Date;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Roberto E. Escobar
 */
public interface TxOrcsData extends TransactionReadable {

   @Override
   int getAuthorId();

   @Override
   Long getBranchId();

   @Override
   String getComment();

   @Override
   int getCommit();

   @Override
   Date getDate();

   @Override
   TransactionDetailsType getTxType();

   void setLocalId(Integer localId);

   void setAuthorId(int authorId);

   void setBranchId(Long branchId);

   void setComment(String comment);

   void setCommit(int commitId);

   void setDate(Date date);

   void setTxType(TransactionDetailsType type);

}