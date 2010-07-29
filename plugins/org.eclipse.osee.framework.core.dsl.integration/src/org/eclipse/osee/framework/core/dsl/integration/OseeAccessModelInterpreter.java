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
package org.eclipse.osee.framework.core.dsl.integration;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class OseeAccessModelInterpreter implements AccessModelInterpreter {

   private final ArtifactDataProvider provider;

   public OseeAccessModelInterpreter(ArtifactDataProvider provider) {
      this.provider = provider;
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
   public void computeAccessDetails(AccessContext context, Object objectToCheck, Collection<AccessDetail<?>> details) throws OseeCoreException {
      if (provider.isApplicable(objectToCheck)) {
         ArtifactData data = provider.asCastedObject(objectToCheck);
         collectApplicable(context, data, details);
      }
   }

   private void collectApplicable(AccessContext context, ArtifactData artifactData, Collection<AccessDetail<?>> details) throws OseeCoreException {
      processContext(context, artifactData, details);
      for (AccessContext superContext : context.getSuperAccessContexts()) {
         collectApplicable(superContext, artifactData, details);
      }
   }

   private void processContext(AccessContext context, ArtifactData artifactData, Collection<AccessDetail<?>> details) throws OseeCoreException {
      collectRestrictions(artifactData, context.getAccessRules(), details);
      Collection<HierarchyRestriction> restrictions = context.getHierarchyRestrictions();

      Collection<String> guidHierarchy = artifactData.getHierarchy();

      for (HierarchyRestriction hierarchy : restrictions) {
         XArtifactRef artifactRef = hierarchy.getArtifact();
         boolean isInHierarchy = guidHierarchy.contains(artifactRef.getGuid());
         if (isInHierarchy) {
            collectRestrictions(artifactData, hierarchy.getAccessRules(), details);
         }
      }
   }

   private void collectRestrictions(ArtifactData artifactData, Collection<ObjectRestriction> restrictions, Collection<AccessDetail<?>> details) throws OseeCoreException {
      for (ObjectRestriction objectRestriction : restrictions) {
         AccessDetail<?> access = getAccess(objectRestriction, artifactData);
         if (access != null) {
            if (!details.contains(access)) {
               details.add(access);
            }
         }
      }
   }

   private AccessDetail<?> getAccess(ObjectRestriction restriction, ArtifactData artifactData) throws OseeCoreException {
      AccessDetail<?> toReturn = null;
      if (restriction instanceof ArtifactInstanceRestriction) {
         toReturn = toAccessDetail((ArtifactInstanceRestriction) restriction, artifactData);
      } else if (restriction instanceof ArtifactTypeRestriction) {
         toReturn = toAccessDetail((ArtifactTypeRestriction) restriction, artifactData);
      } else if (restriction instanceof AttributeTypeRestriction) {
         toReturn = toAccessDetail((AttributeTypeRestriction) restriction, artifactData);
      } else if (restriction instanceof RelationTypeRestriction) {
         toReturn = toAccessDetail((RelationTypeRestriction) restriction, artifactData);
      }
      return toReturn;
   }

   private AccessDetail<?> toAccessDetail(ArtifactInstanceRestriction restriction, ArtifactData artifactData) throws OseeCoreException {
      AccessDetail<?> toReturn = null;
      XArtifactRef artifactRef = restriction.getArtifactRef();
      if (artifactRef.getGuid().equals(artifactData.getGuid())) {
         PermissionEnum premission = OseeUtil.getPermission(restriction);
         toReturn = new AccessDetail<IBasicArtifact<?>>(artifactData.getObject(), premission);
      }
      return toReturn;
   }

   private AccessDetail<?> toAccessDetail(ArtifactTypeRestriction restriction, ArtifactData artifactData) throws OseeCoreException {
      AccessDetail<?> toReturn = null;
      XArtifactType artifactTypeRef = restriction.getArtifactTypeRef();
      IArtifactType typeToMatch = OseeUtil.toToken(artifactTypeRef);

      ArtifactType artifactType = artifactData.getArtifactType();
      boolean isOfType = artifactType != null && artifactType.inheritsFrom(typeToMatch);
      if (isOfType) {
         PermissionEnum premission = OseeUtil.getPermission(restriction);
         toReturn = new AccessDetail<IArtifactType>(artifactType, premission);
      }
      return toReturn;
   }

   private AccessDetail<?> toAccessDetail(AttributeTypeRestriction restriction, ArtifactData artifactData) throws OseeCoreException {
      AccessDetail<?> toReturn = null;
      XAttributeType attributeTypeRef = restriction.getAttributeTypeRef();
      IAttributeType attributeTypeToMatch = OseeUtil.toToken(attributeTypeRef);
      boolean isApplicable = artifactData.isAttributeTypeValid(attributeTypeToMatch);
      if (isApplicable) {
         XArtifactType artifactTypeRef = restriction.getArtifactTypeRef();
         if (artifactTypeRef != null) {
            isApplicable = false;
            IArtifactType typeToMatch = OseeUtil.toToken(artifactTypeRef);
            ArtifactType artifactType = artifactData.getArtifactType();
            isApplicable = artifactType.inheritsFrom(typeToMatch);
         }
      }

      if (isApplicable) {
         PermissionEnum premission = OseeUtil.getPermission(restriction);
         toReturn = new AccessDetail<IAttributeType>(attributeTypeToMatch, premission);
      }
      return toReturn;
   }

   private AccessDetail<?> toAccessDetail(RelationTypeRestriction restriction, ArtifactData artifactData) throws OseeCoreException {
      AccessDetail<?> toReturn = null;

      XRelationType relationTypeRef = restriction.getRelationTypeRef();
      XRelationSideEnum restrictedSide = restriction.getRestrictedToSide();

      IRelationType typeToMatch = OseeUtil.toToken(relationTypeRef);
      RelationType relationType = getRelationTypes(typeToMatch, artifactData);
      if (relationType != null) {
         ArtifactType artifactType = artifactData.getArtifactType();
         for (RelationSide relationSide : RelationSide.values()) {
            if (OseeUtil.isRestrictedSide(restrictedSide, relationSide)) {

               boolean isApplicable = relationType.isArtifactTypeAllowed(relationSide, artifactType);
               if (isApplicable) {
                  PermissionEnum premission = OseeUtil.getPermission(restriction);
                  toReturn =
                     new AccessDetail<RelationTypeSide>(new RelationTypeSide(relationType, relationSide), premission);
               }
            }
         }
      }
      return toReturn;
   }

   private RelationType getRelationTypes(IRelationType typeToMatch, ArtifactData artifact) throws OseeCoreException {
      RelationType toReturn = null;
      Collection<RelationType> relationTypes = artifact.getValidRelationTypes();
      for (RelationType relationType : relationTypes) {
         if (relationType.equals(typeToMatch)) {
            toReturn = relationType;
            break;
         }
      }
      return toReturn;
   }
}
