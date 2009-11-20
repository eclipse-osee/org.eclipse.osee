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

import org.eclipse.osee.framework.core.data.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class BasicArtifactTranslator implements ITranslator<IBasicArtifact<?>> {

   private enum Entry {
      ARTIFACT_NAME,
      ARTIFACT_GUID,
      ARTIFACT_ID,
   }

   public BasicArtifactTranslator() {
   }

   public IBasicArtifact<?> convert(PropertyStore propertyStore) throws OseeCoreException {
      String guid = propertyStore.get(Entry.ARTIFACT_GUID.name());
      String name = propertyStore.get(Entry.ARTIFACT_NAME.name());
      int artId = propertyStore.getInt(Entry.ARTIFACT_ID.name());
      if (!Strings.isValid(guid)) {
         guid = null;
      }
      if (!Strings.isValid(name)) {
         name = null;
      }
      return new DefaultBasicArtifact(artId, guid, name);
   }

   public PropertyStore convert(IBasicArtifact<?> data) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.ARTIFACT_ID.name(), data.getArtId());
      store.put(Entry.ARTIFACT_GUID.name(), data.getGuid());
      store.put(Entry.ARTIFACT_NAME.name(), data.getName());
      return store;
   }

}
