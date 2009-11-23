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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeTranslator implements ITranslator<ArtifactType> {

   private enum Entry{
      GUID,
      NAME,
      IS_ABSTRACT;
   }
   @Override
   public ArtifactType convert(PropertyStore propertyStore) throws OseeCoreException {
      propertyStore.get(Entry.GUID.name());
      propertyStore.get(Entry.NAME.name());
      propertyStore.get(Entry.IS_ABSTRACT.name());
      
      return null;
   }

   @Override
   public PropertyStore convert(ArtifactType artifactType) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      
      store.put(Entry.GUID.name(), artifactType.getGuid());
      store.put(Entry.NAME.name(), artifactType.getName());
      store.put(Entry.IS_ABSTRACT.name(), artifactType.isAbstract());
      
      return store;
   }

}
