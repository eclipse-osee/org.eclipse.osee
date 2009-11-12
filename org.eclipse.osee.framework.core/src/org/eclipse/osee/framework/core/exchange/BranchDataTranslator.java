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
package org.eclipse.osee.framework.core.exchange;

import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public final class BranchDataTranslator implements IDataTranslator<Branch> {

   private enum Entry {
   }

   private final IDataTranslationService service;

   public BranchDataTranslator(IDataTranslationService service) {
      this.service = service;
   }

   private IDataTranslationService getService() {
      return service;
   }

   public Branch convert(PropertyStore propertyStore) throws OseeCoreException {
      IDataTranslationService service = getService();
      // TODO need a basicBranch object similar to basicArtifact - name, id, guid, parentId;
      return null;
   }

   public PropertyStore convert(Branch data) throws OseeCoreException {
      IDataTranslationService service = getService();

      PropertyStore store = new PropertyStore();
      //      store.put(Entry.isArchiveAllowed.name(), data.isArchiveAllowed());

      return store;
   }

}
