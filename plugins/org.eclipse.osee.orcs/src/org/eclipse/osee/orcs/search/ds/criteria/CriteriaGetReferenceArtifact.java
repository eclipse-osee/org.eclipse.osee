/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.search.ds.criteria;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.search.ds.Criteria;

public final class CriteriaGetReferenceArtifact extends Criteria {
   private final AttributeTypeToken attributeType;
   private final BranchId branchId;

   /**
    * @param terminalFollow true if this is the last (terminal) follow in this chain of follows for this (sub) query
    */
   public CriteriaGetReferenceArtifact(BranchId branchId, AttributeTypeToken attributeType) {
      this.branchId = branchId;
      this.attributeType = attributeType;

   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public boolean isReferenceHandler() {
      return true;
   }

   public BranchId getBranchId() {
      return branchId;
   }
}