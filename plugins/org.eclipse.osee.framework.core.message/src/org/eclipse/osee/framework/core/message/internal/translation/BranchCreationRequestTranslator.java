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
package org.eclipse.osee.framework.core.message.internal.translation;

import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.message.BranchCreationRequest;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class BranchCreationRequestTranslator implements ITranslator<BranchCreationRequest> {

   private static enum Fields {
      BRANCH_NAME,
      PARENT_BRANCH_ID,
      ASSOCIATED_ART_ID,

      BRANCH_TYPE,
      SOURCE_TX_ID,
      BRANCH_GUID,
      BRANCH_UUID,

      AUTHOR_ID,

      CREATION_COMMENT,
      MERGE_ADDRESSING_QUERY_ID,
      MERGE_DESTINATION_BRANCH_ID,
      TX_COPY_BRANCH_TYPE;
   }

   @Override
   public BranchCreationRequest convert(PropertyStore store) {
      String branchName = store.get(Fields.BRANCH_NAME.name());
      long parentBranchId = store.getLong(Fields.PARENT_BRANCH_ID.name());
      int associatedArtifactId = store.getInt(Fields.ASSOCIATED_ART_ID.name());

      BranchType branchType = BranchType.valueOf(store.get(Fields.BRANCH_TYPE.name()));
      int sourceTransactionId = store.getInt(Fields.SOURCE_TX_ID.name());
      String branchGuid = store.get(Fields.BRANCH_GUID.name());

      int authorId = store.getInt(Fields.AUTHOR_ID.name());

      String creationComment = store.get(Fields.CREATION_COMMENT.name());

      boolean isTxCopy = store.getBoolean(Fields.TX_COPY_BRANCH_TYPE.name());

      int mergeAddressingQueryId = store.getInt(Fields.MERGE_ADDRESSING_QUERY_ID.name());
      int destinationBranchId = store.getInt(Fields.MERGE_DESTINATION_BRANCH_ID.name());
      long branchUuid = store.getLong(Fields.BRANCH_UUID.name());

      BranchCreationRequest branchCreationRequest =
         new BranchCreationRequest(branchType, sourceTransactionId, parentBranchId, branchGuid, branchName, branchUuid,
            associatedArtifactId, authorId, creationComment, mergeAddressingQueryId, destinationBranchId);

      branchCreationRequest.setTxIsCopied(isTxCopy);

      return branchCreationRequest;
   }

   @Override
   public PropertyStore convert(BranchCreationRequest object) {
      PropertyStore store = new PropertyStore();
      store.put(Fields.BRANCH_NAME.name(), object.getBranchName());
      store.put(Fields.PARENT_BRANCH_ID.name(), object.getParentBranchId());
      store.put(Fields.ASSOCIATED_ART_ID.name(), object.getAssociatedArtifactId());
      store.put(Fields.BRANCH_TYPE.name(), object.getBranchType().name());
      store.put(Fields.SOURCE_TX_ID.name(), object.getSourceTransactionId());
      store.put(Fields.BRANCH_GUID.name(), object.getBranchGuid());
      store.put(Fields.BRANCH_UUID.name(), object.getBranchUuid());
      store.put(Fields.AUTHOR_ID.name(), object.getAuthorId());
      store.put(Fields.CREATION_COMMENT.name(), object.getCreationComment());

      store.put(Fields.MERGE_ADDRESSING_QUERY_ID.name(), object.getMergeAddressingQueryId());
      store.put(Fields.MERGE_DESTINATION_BRANCH_ID.name(), object.getMergeDestinationBranchId());
      store.put(Fields.TX_COPY_BRANCH_TYPE.name(), object.txIsCopied());

      return store;
   }

}
