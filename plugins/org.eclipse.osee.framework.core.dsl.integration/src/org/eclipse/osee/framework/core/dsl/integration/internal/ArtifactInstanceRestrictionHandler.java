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

import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeUtil;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactInstanceRestrictionHandler implements RestrictionHandler<ArtifactInstanceRestriction> {

   @Override
   public ArtifactInstanceRestriction asCastedObject(ObjectRestriction objectRestriction) {
      ArtifactInstanceRestriction toReturn = null;
      if (objectRestriction instanceof ArtifactInstanceRestriction) {
         toReturn = (ArtifactInstanceRestriction) objectRestriction;
      }
      return toReturn;
   }

   @Override
   public void process(ObjectRestriction objectRestriction, ArtifactData artifactData, AccessDetailCollector collector) throws OseeCoreException {
      ArtifactInstanceRestriction restriction = asCastedObject(objectRestriction);
      if (restriction != null) {
         XArtifactRef artifactRef = restriction.getArtifactRef();
         if (artifactRef.getGuid().equals(artifactData.getGuid())) {
            PermissionEnum permission = OseeUtil.getPermission(restriction);
            collector.collect(new AccessDetail<IBasicArtifact<?>>(artifactData.getObject(), permission));
         }
      }
   }

}
