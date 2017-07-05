/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsAttributeResolverService implements IAttributeResolver {

   @Override
   public ArtifactId getSoleArtifactIdReference(IAtsObject atsObject, AttributeTypeToken artifactReferencedAttributeType, ArtifactId defaultValue) {
      ArtifactId result = defaultValue;
      String id = getSoleAttributeValueAsString(atsObject, artifactReferencedAttributeType, "");
      if (Strings.isNumeric(id)) {
         result = ArtifactId.valueOf(id);
      }
      return result;
   }

   @Override
   public ArtifactId getSoleArtifactIdReference(ArtifactToken art, AttributeTypeToken artifactReferencedAttributeType, ArtifactId defaultValue) {
      ArtifactId result = defaultValue;
      String id = getSoleAttributeValueAsString(art, artifactReferencedAttributeType, "");
      if (Strings.isNumeric(id)) {
         result = ArtifactId.valueOf(id);
      }
      return result;
   }

}
