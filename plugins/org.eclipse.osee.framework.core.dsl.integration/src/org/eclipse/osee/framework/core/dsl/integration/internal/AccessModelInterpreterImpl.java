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
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AccessModelInterpreterImpl implements AccessModelInterpreter {

   private final ArtifactDataProvider provider;
   private final ArtifactMatchInterpreter matcher;
   private final Collection<RestrictionHandler<?>> restrictionHandlers;

   public AccessModelInterpreterImpl(ArtifactDataProvider provider, ArtifactMatchInterpreter matcher, RestrictionHandler<?>... restricitionHandlers) {
      this.provider = provider;
      this.matcher = matcher;
      this.restrictionHandlers = new HashSet<>();
      for (RestrictionHandler<?> handler : restricitionHandlers) {
         restrictionHandlers.add(handler);
      }
   }

   @Override
   public AccessContext getContext(Collection<AccessContext> contexts, IAccessContextId contextId)  {
      Conditions.checkNotNull(contexts, "accessContext collection");
      Conditions.checkNotNull(contextId, "accessContextId");
      AccessContext toReturn = null;
      for (AccessContext accessContext : contexts) {
         if (contextId.getGuid().equals(Strings.unquote(accessContext.getGuid()))) {
            toReturn = accessContext;
         }
      }
      return toReturn;
   }

   @Override
   public void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck)  {
      Conditions.checkNotNull(collector, "accessDetailCollector");
      Conditions.checkNotNull(context, "accessContext");
      Conditions.checkNotNull(objectToCheck, "objectToCheck");

      if (provider.isApplicable(objectToCheck)) {
         ArtifactProxy data = provider.asCastedObject(objectToCheck);
         Conditions.checkNotNull(data, "artifactData",
            "artifact data provider returned null - provider has an isApplicable error");

         collectApplicable(collector, context, data);
      }
   }

   private void collectApplicable(AccessDetailCollector collector, AccessContext context, ArtifactProxy artifactData)  {
      Scope scope = getScope(context);
      processContext(collector, context, artifactData, scope);

      for (AccessContext superContext : context.getSuperAccessContexts()) {
         collectApplicable(collector, superContext, artifactData);
      }
   }

   private Scope getScope(AccessContext context) {
      Scope scope = new Scope();
      scopeHelper(scope, context);
      return scope;
   }

   private void scopeHelper(Scope scope, AccessContext context) {
      for (AccessContext parent : context.getSuperAccessContexts()) {
         scopeHelper(scope, parent);
      }
      scope.add(context.getName());
   }

   private void processContext(AccessDetailCollector collector, AccessContext context, ArtifactProxy artifactData, Scope scope)  {
      collectRestrictions(collector, artifactData, context.getAccessRules(), scope);

      Collection<HierarchyRestriction> restrictions = context.getHierarchyRestrictions();
      Collection<ArtifactProxy> proxyHierarchy = artifactData.getHierarchy();

      for (HierarchyRestriction hierarchy : restrictions) {
         XArtifactMatcher artifactRef = hierarchy.getArtifactMatcherRef();
         if (matcher.matches(artifactRef, proxyHierarchy)) {
            String tag = String.format("childOf-%s", artifactRef.getName());
            Scope child = scope.clone().addSubPath(tag);
            collectRestrictions(collector, artifactData, hierarchy.getAccessRules(), child);
         }
      }
   }

   private void collectRestrictions(AccessDetailCollector collector, ArtifactProxy artifactData, Collection<ObjectRestriction> restrictions, Scope scope)  {
      for (ObjectRestriction objectRestriction : restrictions) {
         for (RestrictionHandler<?> restrictionHandler : restrictionHandlers) {
            restrictionHandler.process(objectRestriction, artifactData, collector, scope);
         }
      }
   }

}
