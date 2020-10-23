/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.dsl.integration.internal;

import org.eclipse.osee.framework.core.access.AccessDetail;
import org.eclipse.osee.framework.core.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.access.Scope;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;

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
   public void process(ObjectRestriction objectRestriction, ArtifactProxy artifactProxy, AccessDetailCollector collector, Scope scope) {
      ArtifactTypeRestriction restriction = asCastedObject(objectRestriction);
      if (restriction != null) {
         XArtifactType artifactTypeRef = restriction.getArtifactTypeRef();
         ArtifactTypeToken typeToMatch = OseeUtil.toToken(artifactTypeRef);

         if (artifactProxy.isOfType(typeToMatch)) {
            PermissionEnum permission = OseeUtil.getPermission(restriction);
            collector.collect(new AccessDetail<ArtifactTypeToken>(artifactProxy.getArtifactType(), permission, scope));
         }
      }
   }
}