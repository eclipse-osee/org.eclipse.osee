/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute.service;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeAdapter;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactAttributeAdapter implements AttributeAdapter<Artifact> {

   @Override
   public Artifact adapt(Attribute<?> attribute, Id identity) throws OseeCoreException {
      Long uuid = identity.getId();
      if (uuid <= 0L) {
         return null;
      }
      return ArtifactQuery.getArtifactFromId(new Long(uuid).intValue(), CoreBranches.COMMON,
         DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public Collection<AttributeTypeId> getSupportedTypes() {
      return Collections.singleton(CoreAttributeTypes.ArtifactReference);
   }
}