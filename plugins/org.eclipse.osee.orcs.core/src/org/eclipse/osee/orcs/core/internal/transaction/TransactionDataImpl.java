/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.TransactionData;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class TransactionDataImpl implements TransactionData {

   private final IOseeBranch branch;
   private final ArtifactReadable readable;
   private final String comment;
   private final List<ArtifactTransactionData> data;

   public TransactionDataImpl(IOseeBranch branch, ArtifactReadable readable, String comment, List<ArtifactTransactionData> data) {
      super();
      this.branch = branch;
      this.readable = readable;
      this.comment = comment;
      this.data = data;
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

   @Override
   public ArtifactReadable getAuthor() {
      return readable;
   }

   @Override
   public String getComment() {
      return comment;
   }

   @Override
   public List<ArtifactTransactionData> getTxData() {
      return data;
   }

   @Override
   public void accept(OrcsVisitor visitor) throws OseeCoreException {
      for (ArtifactTransactionData data : getTxData()) {
         data.accept(visitor);
      }
   }

   @Override
   public String toString() {
      return "TransactionDataImpl [branch=" + branch + ", readable=" + readable + ", comment=" + comment + ", data=" + data + "]";
   }
}
