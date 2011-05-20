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
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.model.type.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeRestrictionHandler implements RestrictionHandler<RelationTypeRestriction> {

   private final ArtifactMatchInterpreter matcherInterpreter;

   public RelationTypeRestrictionHandler(ArtifactMatchInterpreter matcherInterpreter) {
      this.matcherInterpreter = matcherInterpreter;
   }

   @Override
   public RelationTypeRestriction asCastedObject(ObjectRestriction objectRestriction) {
      RelationTypeRestriction toReturn = null;
      if (objectRestriction instanceof RelationTypeRestriction) {
         toReturn = (RelationTypeRestriction) objectRestriction;
      }
      return toReturn;
   }

   @Override
   public void process(ObjectRestriction objectRestriction, ArtifactProxy artifactProxy, AccessDetailCollector collector, Scope scope) throws OseeCoreException {
      RelationTypeRestriction restriction = asCastedObject(objectRestriction);
      if (restriction != null) {
         XRelationType relationTypeRef = restriction.getRelationTypeRef();
         XRelationSideEnum restrictedSide = restriction.getRestrictedToSide();
         XArtifactMatcher artifactMatcher = restriction.getArtifactMatcherRef();
         Scope toUse = scope;

         if (artifactMatcher != null && matcherInterpreter != null) {
            if (matcherInterpreter.matches(artifactMatcher, artifactProxy)) {
               toUse = scope.clone().addSubPath(artifactProxy.getName());
            }
         }

         IRelationType typeToMatch = OseeUtil.toToken(relationTypeRef);
         RelationType relationType = getRelationType(typeToMatch, artifactProxy);
         if (relationType != null) {
            for (RelationSide relationSide : RelationSide.values()) {
               if (OseeUtil.isRestrictedSide(restrictedSide, relationSide)) {
                  PermissionEnum permission = OseeUtil.getPermission(restriction);
                  collector.collect(new AccessDetail<RelationTypeSide>(
                     new RelationTypeSide(relationType, relationSide), permission, toUse));
               }
            }
         }
      }
   }

   private RelationType getRelationType(IRelationType typeToMatch, ArtifactProxy artifactProxy) throws OseeCoreException {
      RelationType toReturn = null;
      Collection<RelationType> relationTypes = artifactProxy.getValidRelationTypes();
      for (RelationType relationType : relationTypes) {
         if (relationType.equals(typeToMatch)) {
            toReturn = relationType;
            break;
         }
      }
      return toReturn;
   }
}
