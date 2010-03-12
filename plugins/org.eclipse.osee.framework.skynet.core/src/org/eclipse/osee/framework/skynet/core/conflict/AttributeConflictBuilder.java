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

package org.eclipse.osee.framework.skynet.core.conflict;

import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Theron Virgin
 */
public class AttributeConflictBuilder extends ConflictBuilder {

   private final String sourceValue;
   private final int attrId;
   private final int attrTypeId;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param artifact
    * @param transactionType
    * @param mergeBranch
    * @param sourceBranch
    * @param destBranch
    * @param sourceValue
    * @param destValue
    * @param sourceContent
    * @param destContent
    * @param attrId
    * @param attrTypeId
    */
   public AttributeConflictBuilder(int sourceGamma, int destGamma, int artId, TransactionRecord toTransactionId, Branch sourceBranch, Branch destBranch, String sourceValue, int attrId, int attrTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, sourceBranch, destBranch);
      this.sourceValue = sourceValue;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
   }

   @Override
   public Conflict getConflict(Branch mergeBranch, Set<Integer> artIdSet) throws OseeCoreException {
      for (Integer integer : artIdSet) {
         if (integer.intValue() == artId) return null;
      }
      AttributeConflict attributeConflict =
            new AttributeConflict(sourceGamma, destGamma, artId, toTransactionId, sourceValue, attrId, attrTypeId,
                  mergeBranch, sourceBranch, destBranch);
      if (attributeConflict.getChangeItem().toString().equals("Word Ole Data")) return null;
      return attributeConflict;
   }

}
