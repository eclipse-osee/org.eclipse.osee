/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.util.OseeDslSwitch;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ComposedChecks;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
//Override the checks in AbstractAtsDslJavaValidator to provide own Name validator
@ComposedChecks(validators = {org.eclipse.xtext.validation.ImportUriValidator.class, OseeNamesAreUniqueValidator.class})
public class OseeDslJavaValidator extends AbstractOseeDslJavaValidator {

   public static final String NON_UNIQUE_HIERARCHY = "non_unique_hierarchy";
   public static final String NON_UNIQUE_ARTIFACT_INSTANCE_RESTRICTION = "non_unique_artifact_instance_restriction";
   public static final String NON_UNIQUE_ARTIFACT_TYPE_RESTRICTION = "non_unique_artifact_type_restriction";
   public static final String NON_UNIQUE_ATTRIBUTE_TYPE_RESTRICTION = "non_unique_attribute_type_restriction";
   public static final String NON_UNIQUE_RELATION_TYPE_RESTRICTION = "non_unique_relation_type_restriction";

   @Check
   public void checkTypeNameValidity(OseeDsl oseeDsl) {
      Set<String> typeNames = new HashSet<String>(50);
      Map<String, String> guidToTypeName = new HashMap<String, String>(500);
      for (XAttributeType attrType : oseeDsl.getAttributeTypes()) {
         if (typeNames.contains(attrType.getName())) {
            String message = String.format("Duplicate attribute type name [%s]", attrType.getName());
            error(message, attrType, OseeDslPackage.XATTRIBUTE_TYPE__NAME);
         } else {
            typeNames.add(attrType.getName());
         }
         if (guidToTypeName.containsKey(attrType.getTypeGuid())) {
            String message =
               String.format("Duplicate guid [%s] for attribute types [%s] and [%s]", attrType.getTypeGuid(),
                  attrType.getName(), guidToTypeName.get(attrType.getTypeGuid()));
            error(message, attrType, OseeDslPackage.XATTRIBUTE_TYPE__TYPE_GUID);
         } else {
            guidToTypeName.put(attrType.getTypeGuid(), attrType.getName());
         }
      }
      typeNames.clear();
      guidToTypeName.clear();
      for (XArtifactType artType : oseeDsl.getArtifactTypes()) {
         if (typeNames.contains(artType.getName())) {
            String message = String.format("Duplicate artifact type name [%s]", artType.getName());
            error(message, artType, OseeDslPackage.XARTIFACT_TYPE__NAME);
         } else {
            typeNames.add(artType.getName());
         }
         if (guidToTypeName.containsKey(artType.getTypeGuid())) {
            String message =
               String.format("Duplicate guid [%s] for artifact types [%s] and [%s]", artType.getTypeGuid(),
                  artType.getName(), guidToTypeName.get(artType.getTypeGuid()));
            error(message, artType, OseeDslPackage.XARTIFACT_TYPE__TYPE_GUID);
         } else {
            guidToTypeName.put(artType.getTypeGuid(), artType.getName());
         }
      }
      typeNames.clear();
      guidToTypeName.clear();
      for (XRelationType relType : oseeDsl.getRelationTypes()) {
         if (typeNames.contains(relType.getName())) {
            String message = String.format("Duplicate relation type name [%s]", relType.getName());
            error(message, relType, OseeDslPackage.XRELATION_TYPE__NAME);
         } else {
            typeNames.add(relType.getName());
         }
         if (guidToTypeName.containsKey(relType.getTypeGuid())) {
            String message =
               String.format("Duplicate guid [%s] for relation types [%s] and [%s]", relType.getTypeGuid(),
                  relType.getName(), guidToTypeName.get(relType.getTypeGuid()));
            error(message, relType, OseeDslPackage.XRELATION_TYPE__TYPE_GUID);
         } else {
            guidToTypeName.put(relType.getTypeGuid(), relType.getName());
         }
      }
   }

   @Check
   public void checkAccessContextRulesUnique(AccessContext accessContext) {
      checkObjectRestrictions(accessContext, accessContext.getAccessRules());
      checkHierarchyUnique(accessContext, accessContext.getHierarchyRestrictions());
   }

   private void checkHierarchyUnique(AccessContext accessContext, Collection<HierarchyRestriction> hierarchy) {
      Map<String, XArtifactRef> references = new HashMap<String, XArtifactRef>();
      for (HierarchyRestriction restriction : hierarchy) {
         XArtifactRef artifactRef = restriction.getArtifact();
         String guid = artifactRef.getGuid();
         XArtifactRef reference = references.get(guid);
         if (reference == null) {
            references.put(guid, artifactRef);
         } else {
            String message =
               String.format("Duplicate hierarchy restriction [%s] in context[%s]", reference.toString(),
                  accessContext.getName());
            error(message, restriction, OseeDslPackage.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS, NON_UNIQUE_HIERARCHY,
               reference.getGuid());
         }
         checkObjectRestrictions(accessContext, restriction.getAccessRules());
      }
   }

   private void checkObjectRestrictions(AccessContext accessContext, Collection<ObjectRestriction> restrictions) {
      CheckSwitch restrictionChecker = new CheckSwitch(accessContext);
      for (ObjectRestriction restriction : restrictions) {
         restrictionChecker.doSwitch(restriction);
      }
   }

   private final class CheckSwitch extends OseeDslSwitch<Object> {
      private final Map<String, XArtifactRef> artInstanceRestrictions = new HashMap<String, XArtifactRef>();
      private final Map<String, XArtifactType> artifactTypeRestrictions = new HashMap<String, XArtifactType>();
      private final Map<String, XRelationType> relationTypeRetrictions = new HashMap<String, XRelationType>();
      private final Collection<AttributeTypeRestriction> attrTypeRetrictions = new HashSet<AttributeTypeRestriction>();

      private final AccessContext accessContext;

      public CheckSwitch(AccessContext accessContext) {
         this.accessContext = accessContext;
      }

      @Override
      public Object caseArtifactInstanceRestriction(ArtifactInstanceRestriction restriction) {
         String guid = restriction.getArtifactRef().getGuid();
         XArtifactRef reference = artInstanceRestrictions.get(guid);
         if (reference == null) {
            artInstanceRestrictions.put(guid, restriction.getArtifactRef());
         } else {
            String message =
               String.format("Duplicate artifact instance restriction [%s] in context[%s]", reference.toString(),
                  accessContext.getName());
            error(message, restriction, OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES,
               NON_UNIQUE_ARTIFACT_INSTANCE_RESTRICTION, reference.getGuid());
         }
         return restriction;
      }

      @Override
      public Object caseArtifactTypeRestriction(ArtifactTypeRestriction restriction) {
         String guid = restriction.getArtifactTypeRef().getTypeGuid();
         XArtifactType reference = artifactTypeRestrictions.get(guid);
         if (reference == null) {
            artifactTypeRestrictions.put(guid, restriction.getArtifactTypeRef());
         } else {
            String message =
               String.format("Duplicate artifact type restriction [%s] in context[%s]", reference.toString(),
                  accessContext.getName());
            error(message, restriction, OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES,
               NON_UNIQUE_ARTIFACT_TYPE_RESTRICTION, reference.getTypeGuid());
         }
         return restriction;
      }

      @Override
      public Object caseAttributeTypeRestriction(AttributeTypeRestriction object) {
         XArtifactType artifactType = object.getArtifactTypeRef();
         String attrGuidToMatch = object.getAttributeTypeRef().getTypeGuid();

         for (AttributeTypeRestriction r1 : attrTypeRetrictions) {
            String storedGuid = r1.getAttributeTypeRef().getTypeGuid();
            if (attrGuidToMatch.equals(storedGuid)) {
               XArtifactType storedArtType = r1.getArtifactTypeRef();
               boolean dispatchError = false;
               if (storedArtType != null && artifactType != null) {
                  dispatchError = storedArtType.getTypeGuid().equals(artifactType.getTypeGuid());
               } else if (storedArtType == null && artifactType == null) {
                  dispatchError = true;
               }

               if (dispatchError) {
                  String message =
                     String.format("Duplicate attribute type restriction [%s] in context[%s]", object.toString(),
                        accessContext.getName());
                  error(message, object, OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES,
                     NON_UNIQUE_ATTRIBUTE_TYPE_RESTRICTION, r1.getAttributeTypeRef().getTypeGuid());
               }
            }
         }

         return object;
      }

      @Override
      public Object caseRelationTypeRestriction(RelationTypeRestriction restriction) {
         restriction.getRelationTypeRef();

         String guid = restriction.getRelationTypeRef().getTypeGuid();
         XRelationType reference = relationTypeRetrictions.get(guid);
         if (reference == null) {
            relationTypeRetrictions.put(guid, restriction.getRelationTypeRef());
         } else {
            String message =
               String.format("Duplicate artifact type restriction [%s] in context[%s]", reference.toString(),
                  accessContext.getName());
            error(message, restriction, OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES,
               NON_UNIQUE_RELATION_TYPE_RESTRICTION, reference.getTypeGuid());
         }
         return restriction;
      }

   }

}
