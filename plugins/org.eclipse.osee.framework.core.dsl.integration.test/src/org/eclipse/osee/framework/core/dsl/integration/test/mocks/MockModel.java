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
package org.eclipse.osee.framework.core.dsl.integration.test.mocks;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslFactory;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * @author Roberto E. Escobar
 */
public final class MockModel {

   private MockModel() {
      // Utility class
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

   public static ArtifactInstanceRestriction createArtifactInstanceRestriction() {
      ArtifactInstanceRestriction toReturn = OseeDslFactory.eINSTANCE.createArtifactInstanceRestriction();
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

   public static XArtifactRef createXArtifactRef(String guid, String name) {
      XArtifactRef toReturn = OseeDslFactory.eINSTANCE.createXArtifactRef();
      Assert.assertNotNull(toReturn);
      toReturn.setGuid(guid);
      toReturn.setName(name);
      Assert.assertEquals(guid, toReturn.getGuid());
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }

   public static XArtifactType createXArtifactType(String guid, String name) {
      XArtifactType toReturn = OseeDslFactory.eINSTANCE.createXArtifactType();
      Assert.assertNotNull(toReturn);
      toReturn.setTypeGuid(guid);
      toReturn.setName(name);
      Assert.assertEquals(guid, toReturn.getTypeGuid());
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }

   public static XAttributeType createXAttributeType(String guid, String name) {
      XAttributeType toReturn = OseeDslFactory.eINSTANCE.createXAttributeType();
      Assert.assertNotNull(toReturn);
      toReturn.setTypeGuid(guid);
      toReturn.setName(name);
      Assert.assertEquals(guid, toReturn.getTypeGuid());
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }

   public static XRelationType createXRelationType(String guid, String name) {
      XRelationType toReturn = OseeDslFactory.eINSTANCE.createXRelationType();
      Assert.assertNotNull(toReturn);
      toReturn.setTypeGuid(guid);
      toReturn.setName(name);
      Assert.assertEquals(guid, toReturn.getTypeGuid());
      Assert.assertEquals(name, toReturn.getName());
      return toReturn;
   }
}
