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

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeTranslator implements ITranslator<RelationType> {

   private enum Entry {
      GUID,
      UNIQUE_ID,
      NAME,
      MOD_TYPE,
      SIDE_A_NAME,
      SIDE_B_NAME,
      ART_TYPE_A,
      ART_TYPE_B,
      MULTIPLICITY,
      ORDER_GUID;
   }

   private final IDataTranslationService service;
   private final IOseeModelFactoryServiceProvider provider;

   public RelationTypeTranslator(IDataTranslationService service, IOseeModelFactoryServiceProvider provider) {
      this.service = service;
      this.provider = provider;
   }

   @Override
   public RelationType convert(PropertyStore store) throws OseeCoreException {
      String guid = store.get(Entry.GUID.name());
      int uniqueId = store.getInt(Entry.UNIQUE_ID.name());
      String name = store.get(Entry.NAME.name());
      ModificationType modType = ModificationType.valueOf(store.get(Entry.MOD_TYPE.name()));

      String sideA = store.get(Entry.SIDE_A_NAME.name());
      String sideB = store.get(Entry.SIDE_B_NAME.name());

      String defaultOrderTypeGuid = store.get(Entry.ORDER_GUID.name());
      RelationTypeMultiplicity multiplicity = RelationTypeMultiplicity.valueOf(store.get(Entry.MULTIPLICITY.name()));

      ArtifactType artifactTypeSideA =
            service.convert(store.getPropertyStore(Entry.ART_TYPE_A.name()), ArtifactType.class);
      ArtifactType artifactTypeSideB =
            service.convert(store.getPropertyStore(Entry.ART_TYPE_B.name()), ArtifactType.class);

      RelationType relType =
            provider.getOseeFactoryService().getRelationTypeFactory().create(guid, name, sideA, sideB,
                  artifactTypeSideA, artifactTypeSideB, multiplicity, defaultOrderTypeGuid);
      relType.setId(uniqueId);
      relType.setModificationType(modType);
      return relType;
   }

   @Override
   public PropertyStore convert(RelationType type) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Entry.GUID.name(), type.getGuid());
      store.put(Entry.UNIQUE_ID.name(), type.getId());
      store.put(Entry.NAME.name(), type.getName());
      store.put(Entry.MOD_TYPE.name(), type.getModificationType().name());

      store.put(Entry.SIDE_A_NAME.name(), type.getSideAName());
      store.put(Entry.SIDE_B_NAME.name(), type.getSideBName());

      store.put(Entry.ORDER_GUID.name(), type.getDefaultOrderTypeGuid());
      store.put(Entry.MULTIPLICITY.name(), type.getMultiplicity().name());

      store.put(Entry.ART_TYPE_A.name(), service.convert(type.getArtifactTypeSideA()));
      store.put(Entry.ART_TYPE_B.name(), service.convert(type.getArtifactTypeSideB()));
      return store;
   }

}
