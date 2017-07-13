/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeAdapter;

/**
 * Artifact Referenced Attributes that point to current Ats Branch
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchArtifactReferenceAttributeAdapter implements AttributeAdapter<Artifact> {

   private static final List<AttributeTypeId> PROGRAM_UUID_LIST =
      Arrays.asList(AtsAttributeTypes.ProgramUuid, AtsAttributeTypes.TeamDefinitionReference,
         AtsAttributeTypes.WorkPackageProgram, AtsAttributeTypes.ActionableItemReference);

   @Override
   public Collection<AttributeTypeId> getSupportedTypes() {
      return PROGRAM_UUID_LIST;
   }

   @Override
   public Artifact adapt(Attribute<?> attribute, Id identity) throws OseeCoreException {
      Artifact resultProgramArt = null;

      int uuid = identity.getId() <= 0 ? 0 : identity.getId().intValue();
      if (uuid > 0) {
         resultProgramArt = ArtifactQuery.getArtifactFromId(uuid, AtsClientService.get().getAtsBranch());
      }
      return resultProgramArt;
   }

}