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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class AccessModelInterpreterProxy implements AccessModelInterpreter {

   private ArtifactDataProvider artifactDataProvider;
   private AccessModelInterpreter proxiedService;

   public void setArtifactDataProvider(ArtifactDataProvider artifactDataProvider) {
      this.artifactDataProvider = artifactDataProvider;
   }

   public void start() {
      ArtifactMatchInterpreter matcher = new ArtifactMatchInterpreter();

      RestrictionHandler<?>[] restrictionHandlers =
         new RestrictionHandler<?>[] {
            new ArtifactMatchRestrictionHandler(matcher),
            new ArtifactTypeRestrictionHandler(),
            new AttributeTypeRestrictionHandler(),
            new RelationTypeRestrictionHandler(matcher)};

      proxiedService = new AccessModelInterpreterImpl(artifactDataProvider, matcher, restrictionHandlers);
   }

   public void stop() {
      proxiedService = null;
   }

   private AccessModelInterpreter getProxiedService() {
      return proxiedService;
   }

   private void checkInitialized() throws OseeCoreException {
      Conditions.checkNotNull(getProxiedService(), "AccessModelInterpreter");
   }

   @Override
   public AccessContext getContext(Collection<AccessContext> contexts, IAccessContextId contextId) throws OseeCoreException {
      checkInitialized();
      return getProxiedService().getContext(contexts, contextId);
   }

   @Override
   public void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck) throws OseeCoreException {
      checkInitialized();
      getProxiedService().computeAccessDetails(collector, context, objectToCheck);
   }
}
