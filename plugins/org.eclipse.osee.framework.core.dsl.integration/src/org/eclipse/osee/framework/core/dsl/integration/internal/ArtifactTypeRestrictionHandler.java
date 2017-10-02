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
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeRestrictionHandler implements RestrictionHandler<ArtifactTypeRestriction> {

   @Override
   public ArtifactTypeRestriction asCastedObject(ObjectRestriction objectRestriction) {
      ArtifactTypeRestriction toReturn = null;
      if (objectRestriction instanceof ArtifactTypeRestriction) {
         toReturn = (ArtifactTypeRestriction) objectRestriction;
      }
      return toReturn;
   }

   @Override
   public void process(ObjectRestriction objectRestriction, ArtifactProxy artifactProxy, AccessDetailCollector collector, Scope scope)  {
      ArtifactTypeRestriction restriction = asCastedObject(objectRestriction);
      if (restriction != null) {
         XArtifactType artifactTypeRef = restriction.getArtifactTypeRef();
         IArtifactType typeToMatch = OseeUtil.toToken(artifactTypeRef);

         ArtifactType artifactType = artifactProxy.getArtifactType();
         boolean isOfType = artifactType != null && artifactType.inheritsFrom(typeToMatch);
         if (isOfType) {
            PermissionEnum permission = OseeUtil.getPermission(restriction);
            collector.collect(new AccessDetail<IArtifactType>(artifactType, permission, scope));
         }
      }
   }

}
