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
import java.util.Collections;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
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
import org.eclipse.osee.framework.core.model.type.ArtifactType;
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
   public void process(ObjectRestriction objectRestriction, ArtifactProxy artifactProxy, AccessDetailCollector collector, Scope scope)  {
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
            ArtifactType artifactType = artifactProxy.getArtifactType();
            if (artifactTypeRef != null && artifactType != null) {
               IArtifactType ruleType =
                  TokenFactory.createArtifactType(Long.valueOf(artifactTypeRef.getId()), artifactTypeRef.getName());
               if (artifactType.inheritsFrom(ruleType)) {
                  toUse = scope.clone().addSubPath(artifactProxy.getName());
               }
            }
         }

         Collection<RelationType> relationTypes = getRelationTypes(artifactProxy, restriction);
         PermissionEnum permission = OseeUtil.getPermission(restriction);

         for (RelationType relationType : relationTypes) {
            for (RelationSide relationSide : RelationSide.values()) {
               if (OseeUtil.isRestrictedSide(restrictedSide, relationSide)) {
                  collector.collect(new AccessDetail<RelationTypeSide>(new RelationTypeSide(relationType, relationSide),
                     permission, toUse));
               }
            }
         }
      }
   }

   private Collection<RelationType> getRelationTypes(ArtifactProxy artifactProxy, RelationTypeRestriction restriction)  {
      Collection<RelationType> types;
      if (restriction.isRelationTypeMatch()) {
         types = artifactProxy.getValidRelationTypes();
      } else {
         XRelationType xRelationType = restriction.getRelationTypeRef();
         IRelationType typeToMatch = OseeUtil.toToken(xRelationType);
         RelationType relationType = getRelationType(typeToMatch, artifactProxy);
         if (relationType != null) {
            types = Collections.singleton(relationType);
         } else {
            types = Collections.emptyList();
         }
      }
      return types;
   }

   private RelationType getRelationType(IRelationType typeToMatch, ArtifactProxy artifactProxy)  {
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
