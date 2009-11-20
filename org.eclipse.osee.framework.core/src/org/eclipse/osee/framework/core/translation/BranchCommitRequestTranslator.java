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
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public final class BranchCommitRequestTranslator implements ITranslator<BranchCommitRequest> {

   private enum Entry {
      userArtifact,
      sourceBranch,
      destinationBranch,
      isArchiveAllowed;
   }

   private final IDataTranslationService service;

   public BranchCommitRequestTranslator(IDataTranslationService service) {
      this.service = service;
   }

   private IDataTranslationService getService() {
      return service;
   }

   public BranchCommitRequest convert(PropertyStore propertyStore) throws OseeCoreException {
      PropertyStore sourceBranchStore = propertyStore.getPropertyStore(Entry.sourceBranch.name());
      PropertyStore destinationBranchStore = propertyStore.getPropertyStore(Entry.destinationBranch.name());
      PropertyStore userArtifactStore = propertyStore.getPropertyStore(Entry.userArtifact.name());

      IDataTranslationService service = getService();
      IBasicArtifact<?> userArtifact = service.convert(userArtifactStore, IBasicArtifact.class);
      Branch sourceBranch = service.convert(sourceBranchStore, Branch.class);
      Branch destinationBranch = service.convert(destinationBranchStore, Branch.class);

      boolean isArchiveAllowed = propertyStore.getBoolean(Entry.isArchiveAllowed.name());
      BranchCommitRequest data =
            new BranchCommitRequest(userArtifact, sourceBranch, destinationBranch, isArchiveAllowed);
      return data;
   }

   public PropertyStore convert(BranchCommitRequest data) throws OseeCoreException {
      IDataTranslationService service = getService();

      PropertyStore store = new PropertyStore();
      store.put(Entry.isArchiveAllowed.name(), data.isArchiveAllowed());
      store.put(Entry.userArtifact.name(), service.convert(data.getUser()));
      store.put(Entry.sourceBranch.name(), service.convert(data.getSourceBranch()));
      store.put(Entry.destinationBranch.name(), service.convert(data.getDestinationBranch()));

      return store;
   }

}
