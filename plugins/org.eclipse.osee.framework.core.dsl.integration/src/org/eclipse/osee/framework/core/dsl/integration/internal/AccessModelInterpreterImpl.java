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
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;

/**
 * @author Roberto E. Escobar
 */
public class AccessModelInterpreterImpl implements AccessModelInterpreter {

   private final ArtifactDataProvider provider;
   private final Collection<RestrictionHandler<?>> restrictionHandlers;

   public AccessModelInterpreterImpl(ArtifactDataProvider provider, RestrictionHandler<?>... restricitionHandlers) {
      this.provider = provider;
      this.restrictionHandlers = new HashSet<RestrictionHandler<?>>();
      for (RestrictionHandler<?> handler : restricitionHandlers) {
         restrictionHandlers.add(handler);
      }
   }

   @Override
   public AccessContext getContext(Collection<AccessContext> contexts, AccessContextId contextId) {
      AccessContext toReturn = null;
      for (AccessContext accessContext : contexts) {
         if (contextId.getGuid().equals(accessContext.getGuid())) {
            toReturn = accessContext;
         }
      }
      return toReturn;
   }

   @Override
   public void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck) throws OseeCoreException {
      if (provider.isApplicable(objectToCheck)) {
         ArtifactData data = provider.asCastedObject(objectToCheck);
         collectApplicable(collector, context, data);
      }
   }

   private void collectApplicable(AccessDetailCollector collector, AccessContext context, ArtifactData artifactData) throws OseeCoreException {
      processContext(collector, context, artifactData);
      for (AccessContext superContext : context.getSuperAccessContexts()) {
         collectApplicable(collector, superContext, artifactData);
      }
   }

   private void processContext(AccessDetailCollector collector, AccessContext context, ArtifactData artifactData) throws OseeCoreException {
      collectRestrictions(collector, artifactData, context.getAccessRules());
      Collection<HierarchyRestriction> restrictions = context.getHierarchyRestrictions();

      Collection<String> guidHierarchy = artifactData.getHierarchy();

      for (HierarchyRestriction hierarchy : restrictions) {
         XArtifactRef artifactRef = hierarchy.getArtifact();
         boolean isInHierarchy = guidHierarchy.contains(artifactRef.getGuid());
         if (isInHierarchy) {
            collectRestrictions(collector, artifactData, hierarchy.getAccessRules());
         }
      }
   }

   private void collectRestrictions(AccessDetailCollector collector, ArtifactData artifactData, Collection<ObjectRestriction> restrictions) throws OseeCoreException {
      for (ObjectRestriction objectRestriction : restrictions) {
         for (RestrictionHandler<?> restrictionHandler : restrictionHandlers) {
            restrictionHandler.process(objectRestriction, artifactData, collector);
         }
      }
   }
}
