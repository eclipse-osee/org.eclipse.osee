/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsAttributeResolverService implements IAttributeResolver {

   @Override
   public ArtifactId getSoleArtifactIdReference(IAtsObject atsObject, AttributeTypeToken artifactReferencedAttributeType, ArtifactId defaultValue) {
      return getSoleAttributeValue(atsObject, artifactReferencedAttributeType, ArtifactId.SENTINEL);
   }

   @Override
   public ArtifactId getSoleArtifactIdReference(ArtifactToken art, AttributeTypeToken artifactReferencedAttributeType, ArtifactId defaultValue) {
      return getSoleAttributeValue(art, artifactReferencedAttributeType, defaultValue);
   }

   @Override
   public Collection<ArtifactId> getArtifactIdReferences(ArtifactToken artifact, AttributeTypeToken artifactReferencedAttributeType) {
      return getAttributeValues(artifact, artifactReferencedAttributeType);
   }
}