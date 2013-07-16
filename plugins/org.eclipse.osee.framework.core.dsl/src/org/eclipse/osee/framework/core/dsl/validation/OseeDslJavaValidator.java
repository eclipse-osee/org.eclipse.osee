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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactPredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypePredicate;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.util.OseeDslSwitch;
import org.eclipse.xtext.util.Strings;
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
   public void checkAttributeValidity(XAttributeType attribute) {
      String min = attribute.getMin();
      int minOccurrences = 0;
      if (!Strings.isEmpty(min)) {
         minOccurrences = Integer.parseInt(min);
         if (minOccurrences > 0 && Strings.isEmpty(attribute.getDefaultValue())) {
            error("Default value cannot be empty if min greater than 0", attribute,
               OseeDslPackage.Literals.XATTRIBUTE_TYPE__DEFAULT_VALUE);
         }
      }
      if (minOccurrences < 0) {
         error("min cannot be less than 0", attribute, OseeDslPackage.Literals.XATTRIBUTE_TYPE__MIN);
      }

      String max = attribute.getMax();
      int maxOccurrences = 0;
      if (!Strings.isEmpty(max)) {
         maxOccurrences = Integer.parseInt(max);
      }
      if (minOccurrences > maxOccurrences) {
         error("min must not be greater than max", attribute, OseeDslPackage.Literals.XATTRIBUTE_TYPE__MAX);
      }
   }

   @Check
   public void checkUuidValidity(OseeDsl oseeDsl) {
      Map<String, OseeType> uuids = new HashMap<String, OseeType>();
      EStructuralFeature feature = OseeDslPackage.Literals.OSEE_TYPE__UUID;
      int index = OseeDslPackage.OSEE_TYPE__UUID;
      for (EObject object : oseeDsl.eContents()) {
         if (object instanceof OseeType) {
            OseeType type = (OseeType) object;
            uuidValidityHelper(uuids, type, feature, index);
         }
      }
   }

   private void uuidValidityHelper(Map<String, OseeType> uuids, OseeType type, EStructuralFeature feature, int index) {
      String key = type.getUuid();
      OseeType duplicate = uuids.put(key, type);
      if (duplicate != null) {
         String message =
            String.format("Duplicate uuids detected:\nname:[%s] uuid:[%s]\nname:[%s] uuid:[%s]", type.getName(),
               type.getUuid(), duplicate.getName(), duplicate.getUuid());
         error(message, type, feature, index);

         message =
            String.format("Duplicate uuids detected:\nname:[%s] uuid:[%s]\nname:[%s] uuid:[%s]", duplicate.getName(),
               duplicate.getUuid(), type.getName(), type.getUuid());
         error(message, duplicate, feature, index);
      }
   }

   @Check
   public void checkTypeNameValidity(OseeDsl oseeDsl) {
      Set<String> typeNames = new HashSet<String>(50);
      Map<String, String> uuidToTypeName = new HashMap<String, String>(500);
      for (XAttributeType attrType : oseeDsl.getAttributeTypes()) {
         if (typeNames.contains(attrType.getName())) {
            String message = String.format("Duplicate attribute type name [%s]", attrType.getName());
            error(message, attrType, OseeDslPackage.Literals.OSEE_TYPE__NAME, OseeDslPackage.XATTRIBUTE_TYPE__NAME);
         } else {
            typeNames.add(attrType.getName());
         }
         if (uuidToTypeName.containsKey(attrType.getUuid())) {
            String message =
               String.format("Duplicate uuid [%s] for attribute types [%s] and [%s]", attrType.getUuid(),
                  attrType.getName(), uuidToTypeName.get(attrType.getUuid()));
            error(message, attrType, OseeDslPackage.Literals.OSEE_TYPE__UUID, OseeDslPackage.XATTRIBUTE_TYPE__UUID);
         } else {
            uuidToTypeName.put(attrType.getUuid(), attrType.getName());
         }
      }
      typeNames.clear();
      uuidToTypeName.clear();
      for (XArtifactType artType : oseeDsl.getArtifactTypes()) {
         if (typeNames.contains(artType.getName())) {
            String message = String.format("Duplicate artifact type name [%s]", artType.getName());
            error(message, artType, OseeDslPackage.Literals.OSEE_TYPE__NAME, OseeDslPackage.XARTIFACT_TYPE__NAME);
         } else {
            typeNames.add(artType.getName());
         }
         if (uuidToTypeName.containsKey(artType.getUuid())) {
            String message =
               String.format("Duplicate uuid [%s] for artifact types [%s] and [%s]", artType.getUuid(),
                  artType.getName(), uuidToTypeName.get(artType.getUuid()));
            error(message, artType, OseeDslPackage.Literals.OSEE_TYPE__UUID, OseeDslPackage.XARTIFACT_TYPE__UUID);
         } else {
            uuidToTypeName.put(artType.getUuid(), artType.getName());
         }
      }
      typeNames.clear();
      uuidToTypeName.clear();
      for (XRelationType relType : oseeDsl.getRelationTypes()) {
         if (typeNames.contains(relType.getName())) {
            String message = String.format("Duplicate relation type name [%s]", relType.getName());
            error(message, relType, OseeDslPackage.Literals.OSEE_TYPE__NAME, OseeDslPackage.XRELATION_TYPE__NAME);
         } else {
            typeNames.add(relType.getName());
         }
         if (uuidToTypeName.containsKey(relType.getUuid())) {
            String message =
               String.format("Duplicate uuid [%s] for relation types [%s] and [%s]", relType.getUuid(),
                  relType.getName(), uuidToTypeName.get(relType.getUuid()));
            error(message, relType, OseeDslPackage.Literals.OSEE_TYPE__UUID, OseeDslPackage.XRELATION_TYPE__UUID);
         } else {
            uuidToTypeName.put(relType.getUuid(), relType.getName());
         }
      }
   }

   @Check
   public void checkAccessContextRulesUnique(AccessContext accessContext) {
      checkObjectRestrictions(accessContext, accessContext.getAccessRules());
      checkHierarchyUnique(accessContext, accessContext.getHierarchyRestrictions());
   }

   private void checkHierarchyUnique(AccessContext accessContext, Collection<HierarchyRestriction> hierarchy) {
      Map<String, XArtifactMatcher> references = new HashMap<String, XArtifactMatcher>();
      for (HierarchyRestriction restriction : hierarchy) {
         XArtifactMatcher artifactRef = restriction.getArtifactMatcherRef();
         String name = artifactRef.getName();
         XArtifactMatcher reference = references.get(name);
         if (reference == null) {
            references.put(name, artifactRef);
         } else {
            String message =
               String.format("Duplicate hierarchy restriction [%s] in context[%s]", reference.toString(),
                  accessContext.getName());
            error(message, restriction, OseeDslPackage.Literals.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS,
               OseeDslPackage.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS, NON_UNIQUE_HIERARCHY, reference.getName());
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
      private final Map<String, XArtifactMatcher> artInstanceRestrictions = new HashMap<String, XArtifactMatcher>();
      private final Map<String, XArtifactType> artifactTypeRestrictions = new HashMap<String, XArtifactType>();
      private final Map<String, XRelationType> relationTypeRetrictions = new HashMap<String, XRelationType>();
      private final Collection<AttributeTypeRestriction> attrTypeRetrictions = new HashSet<AttributeTypeRestriction>();

      private final AccessContext accessContext;

      public CheckSwitch(AccessContext accessContext) {
         this.accessContext = accessContext;
      }

      @Override
      public Object caseArtifactMatchRestriction(ArtifactMatchRestriction restriction) {
         String name = restriction.getArtifactMatcherRef().getName();
         XArtifactMatcher reference = artInstanceRestrictions.get(name);
         if (reference == null) {
            artInstanceRestrictions.put(name, restriction.getArtifactMatcherRef());
         } else {
            String message =
               String.format("Duplicate artifact instance restriction [%s] in context[%s]", reference.toString(),
                  accessContext.getName());
            error(message, restriction, OseeDslPackage.Literals.ARTIFACT_MATCH_RESTRICTION__ARTIFACT_MATCHER_REF,
               OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES, NON_UNIQUE_ARTIFACT_INSTANCE_RESTRICTION,
               reference.getName());
         }
         return restriction;
      }

      @Override
      public Object caseArtifactTypeRestriction(ArtifactTypeRestriction restriction) {
         String uuid = restriction.getArtifactTypeRef().getUuid();
         XArtifactType reference = artifactTypeRestrictions.get(uuid);
         if (reference == null) {
            artifactTypeRestrictions.put(uuid, restriction.getArtifactTypeRef());
         } else {
            String message =
               String.format("Duplicate artifact type restriction [%s] in context[%s]", reference.toString(),
                  accessContext.getName());
            error(message, restriction, OseeDslPackage.Literals.ARTIFACT_TYPE_RESTRICTION__ARTIFACT_TYPE_REF,
               OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES, NON_UNIQUE_ARTIFACT_TYPE_RESTRICTION, reference.getUuid());
         }
         return restriction;
      }

      @Override
      public Object caseAttributeTypeRestriction(AttributeTypeRestriction object) {
         XArtifactType artifactType = object.getArtifactTypeRef();
         String attrUuidToMatch = object.getAttributeTypeRef().getUuid();

         for (AttributeTypeRestriction r1 : attrTypeRetrictions) {
            String storedUuid = r1.getAttributeTypeRef().getUuid();
            if (attrUuidToMatch.equals(storedUuid)) {
               XArtifactType storedArtType = r1.getArtifactTypeRef();
               boolean dispatchError = false;
               if (storedArtType != null && artifactType != null) {
                  dispatchError = storedArtType.getUuid().equals(artifactType.getUuid());
               } else if (storedArtType == null && artifactType == null) {
                  dispatchError = true;
               }

               if (dispatchError) {
                  String message =
                     String.format("Duplicate attribute type restriction [%s] in context[%s]", object.toString(),
                        accessContext.getName());
                  error(message, object, OseeDslPackage.Literals.ATTRIBUTE_TYPE_RESTRICTION__ARTIFACT_TYPE_REF,
                     OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES, NON_UNIQUE_ATTRIBUTE_TYPE_RESTRICTION,
                     r1.getAttributeTypeRef().getUuid());
               }
            }
         }

         return object;
      }

      @Override
      public Object caseRelationTypeRestriction(RelationTypeRestriction restriction) {
         XRelationType relationTypeRef = restriction.getRelationTypeRef();

         String key = relationTypeRef.getUuid();
         RelationTypePredicate predicate = restriction.getPredicate();
         if (predicate instanceof RelationTypeArtifactPredicate) {
            key += ((RelationTypeArtifactPredicate) predicate).getArtifactMatcherRef().getName();
         } else if (predicate instanceof RelationTypeArtifactTypePredicate) {
            key += ((RelationTypeArtifactTypePredicate) predicate).getArtifactTypeRef().getName();
         }
         XRelationType reference = relationTypeRetrictions.get(key);
         if (reference == null) {
            relationTypeRetrictions.put(key, relationTypeRef);
         } else {
            String message =
               String.format("Duplicate artifact type restriction [%s] in context[%s]", reference.toString(),
                  accessContext.getName());
            error(message, restriction, OseeDslPackage.Literals.RELATION_TYPE_RESTRICTION__RELATION_TYPE_REF,
               OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES, NON_UNIQUE_RELATION_TYPE_RESTRICTION, reference.getUuid());
         }
         return restriction;
      }

   }

}
