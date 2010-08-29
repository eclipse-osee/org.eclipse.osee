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
package org.eclipse.osee.framework.core.dsl.integration.internal;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.type.ArtifactType;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeRestrictionHandler implements RestrictionHandler<AttributeTypeRestriction> {

   @Override
   public AttributeTypeRestriction asCastedObject(ObjectRestriction objectRestriction) {
      AttributeTypeRestriction toReturn = null;
      if (objectRestriction instanceof AttributeTypeRestriction) {
         toReturn = (AttributeTypeRestriction) objectRestriction;
      }
      return toReturn;
   }

   @Override
   public void process(ObjectRestriction objectRestriction, ArtifactData artifactData, AccessDetailCollector collector) throws OseeCoreException {
      AttributeTypeRestriction restriction = asCastedObject(objectRestriction);
      if (restriction != null) {
         XAttributeType attributeTypeRef = restriction.getAttributeTypeRef();
         IAttributeType attributeTypeToMatch = OseeUtil.toToken(attributeTypeRef);
         boolean isApplicable = artifactData.isAttributeTypeValid(attributeTypeToMatch);
         if (isApplicable) {
            XArtifactType artifactTypeRef = restriction.getArtifactTypeRef();
            if (artifactTypeRef != null) {
               isApplicable = false;
               IArtifactType typeToMatch = OseeUtil.toToken(artifactTypeRef);
               ArtifactType artifactType = artifactData.getArtifactType();
               isApplicable = artifactType.inheritsFrom(typeToMatch);
            }
         }

         if (isApplicable) {
            PermissionEnum permission = OseeUtil.getPermission(restriction);
            collector.collect(new AccessDetail<IAttributeType>(attributeTypeToMatch, permission));
         }
      }
   }

}
