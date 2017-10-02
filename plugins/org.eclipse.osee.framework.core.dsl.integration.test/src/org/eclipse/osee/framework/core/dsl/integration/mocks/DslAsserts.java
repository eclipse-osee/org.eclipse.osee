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
package org.eclipse.osee.framework.core.dsl.integration.mocks;

import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.RestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.CompareOp;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Condition;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.MatchField;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.SimpleCondition;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeTypeRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class DslAsserts {

   private DslAsserts() {
      // Utility class
   }

   public static void assertNullAccessDetail(RestrictionHandler<?> handler, ObjectRestriction restriction, ArtifactProxy artifactProxy, Scope expectedScopeLevel)  {
      assertAccessDetail(handler, restriction, artifactProxy, null, null, expectedScopeLevel);
   }

   public static void assertAccessDetail(RestrictionHandler<?> handler, ObjectRestriction restriction, ArtifactProxy artifactProxy, Object expectedAccessObject, PermissionEnum expectedPermission, Scope expectedScopeLevel)  {
      MockAccessDetailCollector collector = new MockAccessDetailCollector();
      handler.process(restriction, artifactProxy, collector, new Scope());
      AccessDetail<?> actualDetail = collector.getAccessDetails();
      if (expectedAccessObject == null) {
         Assert.assertNull(actualDetail);
      } else {
         Assert.assertNotNull(actualDetail);
         Assert.assertEquals(expectedPermission, actualDetail.getPermission());
         Assert.assertEquals(expectedAccessObject, actualDetail.getAccessObject());
         Assert.assertEquals(expectedScopeLevel, actualDetail.getScope());
      }
   }

   public static void assertEquals(OseeDsl model1, OseeDsl model2) {
      Assert.assertEquals(model1.getAccessDeclarations().size(), model2.getAccessDeclarations().size());
      Assert.assertEquals(model1.getArtifactMatchRefs().size(), model2.getArtifactMatchRefs().size());
      Assert.assertEquals(model1.getArtifactTypes().size(), model2.getArtifactTypes().size());
      Assert.assertEquals(model1.getAttributeTypes().size(), model2.getAttributeTypes().size());
      Assert.assertEquals(model1.getEnumOverrides().size(), model2.getEnumOverrides().size());
      Assert.assertEquals(model1.getEnumTypes().size(), model2.getEnumTypes().size());
      Assert.assertEquals(model1.getImports().size(), model2.getImports().size());
      Assert.assertEquals(model1.getRelationTypes().size(), model2.getRelationTypes().size());
   }

   public static void assertEquals(Condition condition, MatchField expField, CompareOp expOp, String expExpression) {
      SimpleCondition simpCondition = (SimpleCondition) condition;
      Assert.assertEquals(expField, simpCondition.getField());
      Assert.assertEquals(expOp, simpCondition.getOp());
      Assert.assertEquals(expExpression, simpCondition.getExpression());
   }

   public static void assertEquals(XArtifactMatcher matcher, String name) {
      Assert.assertEquals(name, matcher.getName());
   }

   public static void assertEquals(XArtifactType artifactType, String expName, String expGuid, String[] inheritsFrom, String... attributeNames) {
      Assert.assertEquals(expName, artifactType.getName());
      Assert.assertEquals(expGuid, artifactType.getId());

      int index = 0;
      Assert.assertEquals(inheritsFrom.length, artifactType.getSuperArtifactTypes().size());
      for (XArtifactType ref : artifactType.getSuperArtifactTypes()) {
         Assert.assertEquals(inheritsFrom[index++], ref.getName());
      }

      index = 0;
      Assert.assertEquals(attributeNames.length, artifactType.getValidAttributeTypes().size());
      for (XAttributeTypeRef ref : artifactType.getValidAttributeTypes()) {
         Assert.assertEquals(attributeNames[index++], ref.getValidAttributeType().getName());
      }
   }

   public static void assertEquals(XAttributeType type, String expName, String expGuid, String baseType, String dataProvider, String min, String max, String tagger, String description, String defaultValue, String ext) {
      Assert.assertEquals(expName, type.getName());
      Assert.assertEquals(expGuid, type.getId());

      Assert.assertEquals(baseType, type.getBaseAttributeType());
      Assert.assertEquals(dataProvider, type.getDataProvider());
      Assert.assertEquals(min, type.getMin());
      Assert.assertEquals(max, type.getMax());
      Assert.assertEquals(tagger, type.getTaggerId());
      Assert.assertEquals(description, type.getDescription());
      Assert.assertEquals(defaultValue, type.getDefaultValue());
      Assert.assertEquals(ext, type.getFileExtension());
   }

   public static void assertEquals(XRelationType type, String expName, String expGuid, String sideA, String aName, String aGuid, String sideB, String bName, String bGuid, String orderType, RelationMultiplicityEnum mult) {
      Assert.assertEquals(expName, type.getName());
      Assert.assertEquals(expGuid, type.getId());

      XArtifactType aArt = type.getSideAArtifactType();
      Assert.assertEquals(sideA, type.getSideAName());
      Assert.assertEquals(aName, aArt.getName());
      Assert.assertEquals(aGuid, aArt.getId());

      XArtifactType bArt = type.getSideBArtifactType();
      Assert.assertEquals(sideB, type.getSideBName());
      Assert.assertEquals(bName, bArt.getName());
      Assert.assertEquals(bGuid, bArt.getId());

      Assert.assertEquals(orderType, type.getDefaultOrderType());
      Assert.assertEquals(mult, type.getMultiplicity());
   }

   public static void assertEquals(AccessContext context, String expName, String expGuid, String[] inheritsFrom) {
      Assert.assertEquals(expName, context.getName());
      Assert.assertEquals(expGuid, context.getGuid());

      int index = 0;
      Assert.assertEquals(inheritsFrom.length, context.getSuperAccessContexts().size());
      for (AccessContext ref : context.getSuperAccessContexts()) {
         Assert.assertEquals(inheritsFrom[index++], ref.getName());
      }
   }

   public static void assertEquals(ArtifactTypeRestriction restriction, AccessPermissionEnum permission, String artTypeName) {
      Assert.assertEquals(permission, restriction.getPermission());
      XArtifactType ref = restriction.getArtifactTypeRef();
      Assert.assertEquals(artTypeName, ref.getName());
   }

   public static void assertEquals(AttributeTypeRestriction restriction, AccessPermissionEnum permission, String attrTypeName, String artTypeName) {
      Assert.assertEquals(permission, restriction.getPermission());

      Assert.assertEquals(attrTypeName, restriction.getAttributeTypeRef().getName());

      XArtifactType ref = restriction.getArtifactTypeRef();
      Assert.assertEquals(artTypeName, ref.getName());
   }

   public static void assertEquals(RelationTypeRestriction restriction, AccessPermissionEnum permission, String relType, XRelationSideEnum sideEnum) {
      Assert.assertEquals(permission, restriction.getPermission());
      Assert.assertEquals(relType, restriction.getRelationTypeRef().getName());
      Assert.assertEquals(sideEnum, restriction.getRestrictedToSide());
   }

   public static void assertEquals(HierarchyRestriction restriction, String matcherName) {
      Assert.assertEquals(matcherName, restriction.getArtifactMatcherRef().getName());
   }
}
