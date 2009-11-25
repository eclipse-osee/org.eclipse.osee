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

import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public final class BranchCommitRequestTranslator implements ITranslator<BranchCommitRequest> {

   private enum Entry {
      USER_ART_ID,
      SRC_BRANCH_ID,
      DEST_BRANCH_ID,
      IS_ARCHIVE_ALLOWED;
   }

   public BranchCommitRequest convert(PropertyStore propertyStore) throws OseeCoreException {
      int srcBranchId = propertyStore.getInt(Entry.SRC_BRANCH_ID.name());
      int destBranchId = propertyStore.getInt(Entry.DEST_BRANCH_ID.name());
      int userArtifactId = propertyStore.getInt(Entry.USER_ART_ID.name());

      boolean isArchiveAllowed = propertyStore.getBoolean(Entry.IS_ARCHIVE_ALLOWED.name());
      BranchCommitRequest data = new BranchCommitRequest(userArtifactId, srcBranchId, destBranchId, isArchiveAllowed);
      return data;
   }

   public PropertyStore convert(BranchCommitRequest data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.IS_ARCHIVE_ALLOWED.name(), data.isArchiveAllowed());
      store.put(Entry.USER_ART_ID.name(), data.getUserArtId());
      store.put(Entry.SRC_BRANCH_ID.name(), data.getSourceBranchId());
      store.put(Entry.DEST_BRANCH_ID.name(), data.getDestinationBranchId());
      return store;
   }

}
