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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.Scope;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatchRestrictionHandler implements RestrictionHandler<ArtifactMatchRestriction> {

   private final ArtifactMatchInterpreter matcherInterpreter;

   public ArtifactMatchRestrictionHandler(ArtifactMatchInterpreter matcherInterpreter) {
      this.matcherInterpreter = matcherInterpreter;
   }

   @Override
   public ArtifactMatchRestriction asCastedObject(ObjectRestriction objectRestriction) {
      ArtifactMatchRestriction toReturn = null;
      if (objectRestriction instanceof ArtifactMatchRestriction) {
         toReturn = (ArtifactMatchRestriction) objectRestriction;
      }
      return toReturn;
   }

   @Override
   public void process(ObjectRestriction objectRestriction, ArtifactProxy artifactProxy, AccessDetailCollector collector, Scope scope)  {
      ArtifactMatchRestriction restriction = asCastedObject(objectRestriction);
      if (restriction != null) {
         XArtifactMatcher artifactMatcher = restriction.getArtifactMatcherRef();
         if (matcherInterpreter.matches(artifactMatcher, artifactProxy)) {
            PermissionEnum permission = OseeUtil.getPermission(restriction);
            collector.collect(new AccessDetail<ArtifactToken>(artifactProxy.getObject(), permission, scope));
         }
      }
   }

}
