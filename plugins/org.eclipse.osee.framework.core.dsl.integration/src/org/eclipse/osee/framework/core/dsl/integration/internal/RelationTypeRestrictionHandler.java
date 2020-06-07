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

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypePredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.Scope;

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
   public void process(ObjectRestriction objectRestriction, ArtifactProxy artifactProxy, AccessDetailCollector collector, Scope scope) {
      RelationTypeRestriction restriction = asCastedObject(objectRestriction);
      if (restriction != null) {
         XRelationSideEnum restrictedSide = restriction.getRestrictedToSide();

         Scope toUse = scope;

         RelationTypePredicate predicate = restriction.getPredicate();
         if (predicate instanceof RelationTypeArtifactPredicate) {
            RelationTypeArtifactPredicate artifactPredicate = (RelationTypeArtifactPredicate) predicate;
            XArtifactMatcher artifactMatcher = artifactPredicate.getArtifactMatcherRef();

            if (artifactMatcher != null && matcherInterpreter != null) {
               if (matcherInterpreter.matches(artifactMatcher, artifactProxy)) {
                  toUse = scope.clone().addSubPath(artifactProxy.getName());
               }
            }
         } else if (predicate instanceof RelationTypeArtifactTypePredicate) {
            RelationTypeArtifactTypePredicate artifactTypePredicate = (RelationTypeArtifactTypePredicate) predicate;
            XArtifactType artifactTypeRef = artifactTypePredicate.getArtifactTypeRef();
            if (artifactTypeRef != null) {
               ArtifactTypeToken ruleType =
                  ArtifactTypeToken.valueOf(Long.valueOf(artifactTypeRef.getId()), artifactTypeRef.getName());
               if (artifactProxy.isOfType(ruleType)) {
                  toUse = scope.clone().addSubPath(artifactProxy.getName());
               }
            }
         }

         Collection<RelationTypeToken> relationTypes = getRelationTypes(artifactProxy, restriction);
         PermissionEnum permission = OseeUtil.getPermission(restriction);

         for (RelationTypeToken relationType : relationTypes) {
            for (RelationSide relationSide : RelationSide.values()) {
               if (OseeUtil.isRestrictedSide(restrictedSide, relationSide)) {
                  collector.collect(
                     new AccessDetail<>(new RelationTypeSide(relationType, relationSide), permission, toUse));
               }
            }
         }
      }
   }

   private Collection<RelationTypeToken> getRelationTypes(ArtifactProxy artifactProxy, RelationTypeRestriction restriction) {
      Collection<RelationTypeToken> types;
      if (restriction.isRelationTypeMatch()) {
         types = artifactProxy.getValidRelationTypes();
      } else {
         XRelationType xRelationType = restriction.getRelationTypeRef();
         RelationTypeToken typeToMatch = OseeUtil.toToken(xRelationType);
         RelationTypeToken relationType = getRelationType(typeToMatch, artifactProxy);
         if (relationType != null) {
            types = Collections.singleton(relationType);
         } else {
            types = Collections.emptyList();
         }
      }
      return types;
   }

   private RelationTypeToken getRelationType(RelationTypeToken typeToMatch, ArtifactProxy artifactProxy) {
      RelationTypeToken toReturn = null;
      Collection<RelationTypeToken> relationTypes = artifactProxy.getValidRelationTypes();
      for (RelationTypeToken relationType : relationTypes) {
         if (relationType.equals(typeToMatch)) {
            toReturn = relationType;
            break;
         }
      }
      return toReturn;
   }
}