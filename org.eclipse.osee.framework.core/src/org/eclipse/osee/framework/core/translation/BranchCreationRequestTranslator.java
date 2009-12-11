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
package org.eclipse.osee.framework.core.translation;

import org.eclipse.osee.framework.core.data.BranchCreationRequest;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class BranchCreationRequestTranslator implements ITranslator<BranchCreationRequest> {

   private enum Fields {
      BRANCH_NAME,
      PARENT_BRANCH_ID,
      ASSOCIATED_ART_ID,

      BRANCH_TYPE,
      SOURCE_TX_ID,
      BRANCH_GUID,

      AUTHOR_ID,

      CREATION_COMMENT,
      POPULATE_BASE_TX_FROM_ADDR_QUERY_ID,
      DESTINATION_BRANCH_ID;
   }

   @Override
   public BranchCreationRequest convert(PropertyStore store) throws OseeCoreException {
      String branchName = store.get(Fields.BRANCH_NAME.name());
      int parentBranchId = store.getInt(Fields.PARENT_BRANCH_ID.name());
      int associatedArtifactId = store.getInt(Fields.ASSOCIATED_ART_ID.name());

      BranchType branchType = BranchType.valueOf(store.get(Fields.BRANCH_TYPE.name()));
      int sourceTransactionId = store.getInt(Fields.SOURCE_TX_ID.name());
      String branchGuid = store.get(Fields.BRANCH_GUID.name());

      int authorId = store.getInt(Fields.AUTHOR_ID.name());

      String creationComment = store.get(Fields.CREATION_COMMENT.name());

      int populateBaseTxFromAddressingQueryId = store.getInt(Fields.POPULATE_BASE_TX_FROM_ADDR_QUERY_ID.name());
      int destinationBranchId = store.getInt(Fields.DESTINATION_BRANCH_ID.name());

      return new BranchCreationRequest(branchType, sourceTransactionId, parentBranchId, branchGuid, branchName,
            associatedArtifactId, authorId, creationComment, populateBaseTxFromAddressingQueryId, destinationBranchId);
   }

   @Override
   public PropertyStore convert(BranchCreationRequest object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Fields.BRANCH_NAME.name(), object.getBranchName());
      store.put(Fields.PARENT_BRANCH_ID.name(), object.getParentBranchId());
      store.put(Fields.ASSOCIATED_ART_ID.name(), object.getAssociatedArtifactId());
      store.put(Fields.BRANCH_TYPE.name(), object.getBranchType().name());
      store.put(Fields.SOURCE_TX_ID.name(), object.getSourceTransactionId());
      store.put(Fields.BRANCH_GUID.name(), object.getBranchGuid());
      store.put(Fields.AUTHOR_ID.name(), object.getAuthorId());
      store.put(Fields.CREATION_COMMENT.name(), object.getCreationComment());

      store.put(Fields.POPULATE_BASE_TX_FROM_ADDR_QUERY_ID.name(), object.getPopulateBaseTxFromAddressingQueryId());
      store.put(Fields.DESTINATION_BRANCH_ID.name(), object.getDestinationBranchId());

      return store;
   }

}
