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

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.dsl.OseeDslResourceUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class MockModel {

   private MockModel() {
      // Utility class
   }

   public static XArtifactMatcher createMatcher(String rawXTextData) {
      XArtifactMatcher toReturn = null;
      try {
         OseeDsl model = OseeDslResourceUtil.loadModel("osee:/text.osee", rawXTextData).getModel();
         toReturn = model.getArtifactMatchRefs().iterator().next();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return toReturn;
   }

   public static OseeDsl createDsl() {
      OseeDsl dsl = OseeDslFactory.eINSTANCE.createOseeDsl();
      Assert.assertNotNull(dsl);
      return dsl;
   }

   public static AccessContext createAccessContext(String guid, String name) {
      AccessContext toReturn = OseeDslFactory.eINSTANCE.createAccessContext();
      Assert.assertNotNull(toReturn);
      toReturn.setGuid(guid);
      toReturn.setName(name);
      Assert.assertEquals(guid, toReturn.getGuid());
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }

   public static ObjectRestriction createObjectRestriction() {
      ObjectRestriction toReturn = OseeDslFactory.eINSTANCE.createObjectRestriction();
      Assert.assertNotNull(toReturn);
      return toReturn;
   }

   public static ArtifactMatchRestriction createArtifactMatchRestriction() {
      ArtifactMatchRestriction toReturn = OseeDslFactory.eINSTANCE.createArtifactMatchRestriction();
      Assert.assertNotNull(toReturn);
      return toReturn;
   }

   public static ArtifactTypeRestriction createArtifactTypeRestriction() {
      ArtifactTypeRestriction toReturn = OseeDslFactory.eINSTANCE.createArtifactTypeRestriction();
      Assert.assertNotNull(toReturn);
      return toReturn;
   }

   public static AttributeTypeRestriction createAttributeTypeRestriction() {
      AttributeTypeRestriction toReturn = OseeDslFactory.eINSTANCE.createAttributeTypeRestriction();
      Assert.assertNotNull(toReturn);
      return toReturn;
   }

   public static RelationTypeRestriction createRelationTypeRestriction() {
      RelationTypeRestriction toReturn = OseeDslFactory.eINSTANCE.createRelationTypeRestriction();
      Assert.assertNotNull(toReturn);
      return toReturn;
   }

   public static XArtifactMatcher createXArtifactMatcherRef(String name) {
      XArtifactMatcher toReturn = OseeDslFactory.eINSTANCE.createXArtifactMatcher();
      Assert.assertNotNull(toReturn);
      toReturn.setName(name);
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }

   public static XArtifactType createXArtifactType(long uuid, String name) {
      XArtifactType toReturn = OseeDslFactory.eINSTANCE.createXArtifactType();
      Assert.assertNotNull(toReturn);
      toReturn.setId(String.valueOf(uuid));
      toReturn.setName(name);
      Assert.assertEquals(uuid, Long.valueOf(toReturn.getId()).longValue());
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }

   public static XAttributeType createXAttributeType(AttributeTypeId id, String name) {
      XAttributeType toReturn = OseeDslFactory.eINSTANCE.createXAttributeType();
      Assert.assertNotNull(toReturn);
      toReturn.setId(id.getIdString());
      toReturn.setName(name);
      Assert.assertEquals(id, Long.valueOf(toReturn.getId()));
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }

   public static XRelationType createXRelationType(RelationTypeToken type) {
      String name = type.getName();
      long id = type.getId();
      XRelationType toReturn = OseeDslFactory.eINSTANCE.createXRelationType();
      Assert.assertNotNull(toReturn);
      toReturn.setId(String.valueOf(id));
      toReturn.setName(name);
      Assert.assertEquals(id, Long.valueOf(toReturn.getId()).longValue());
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }
}
