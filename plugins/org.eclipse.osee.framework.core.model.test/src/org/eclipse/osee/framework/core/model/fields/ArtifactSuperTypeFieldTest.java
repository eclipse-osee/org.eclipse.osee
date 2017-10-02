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
package org.eclipse.osee.framework.core.model.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.core.model.internal.fields.ArtifactSuperTypeField;
import org.eclipse.osee.framework.core.model.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case For {@link ArtifactSuperTypeField}
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactSuperTypeFieldTest {

   private static ArtifactType art1;
   private static ArtifactType art2;
   private static ArtifactType art3;
   private static ArtifactType base;
   private static ArtifactType containingArt;

   @BeforeClass
   public static void prepareTest() {
      containingArt = MockDataFactory.createArtifactType(999);
      art1 = MockDataFactory.createArtifactType(1);
      art2 = MockDataFactory.createArtifactType(2);
      art3 = MockDataFactory.createArtifactType(3);
      base = new ArtifactType(CoreArtifactTypes.Artifact.getGuid(), CoreArtifactTypes.Artifact.getName(), false);
   }

   @Test
   public void testSetGet() {
      List<ArtifactType> input = new ArrayList<>();
      ArtifactSuperTypeField field = new ArtifactSuperTypeField(containingArt, input);
      Assert.assertEquals(false, field.isDirty());

      FieldTestUtil.assertSetGet(field, Arrays.asList(art1, art2, art3), Arrays.asList(art1, art2, art3), true);
      field.clearDirty();
      Assert.assertEquals(false, field.isDirty());

      // Add again in a different order
      FieldTestUtil.assertSetGet(field, Arrays.asList(art3, art1, art2), Arrays.asList(art1, art2, art3), false);

      // Remove
      FieldTestUtil.assertSetGet(field, Arrays.asList(art3), Arrays.asList(art3), true);
      field.clearDirty();
      Assert.assertEquals(false, field.isDirty());

      // Add
      FieldTestUtil.assertSetGet(field, Arrays.asList(art3, art2), Arrays.asList(art3, art2), true);
      field.clearDirty();
      Assert.assertEquals(false, field.isDirty());
   }

   @Test(expected = OseeInvalidInheritanceException.class)
   public void testBaseCircularity() {
      List<ArtifactType> input = new ArrayList<>();
      ArtifactSuperTypeField field = new ArtifactSuperTypeField(containingArt, input);
      Assert.assertEquals(false, field.isDirty());

      field.set(Collections.singletonList(containingArt));
   }

   @Test(expected = OseeInvalidInheritanceException.class)
   public void testBaseArtifact() {
      List<ArtifactType> input = new ArrayList<>();
      ArtifactSuperTypeField field = new ArtifactSuperTypeField(containingArt, input);
      Assert.assertEquals(false, field.isDirty());

      field.set(Collections.<ArtifactType> emptyList());
   }

   @Test
   public void testBaseArtifactNoSuperTypeRequired() {
      List<ArtifactType> input = new ArrayList<>();
      ArtifactSuperTypeField field = new ArtifactSuperTypeField(base, input);
      Assert.assertEquals(false, field.isDirty());

      field.set(Collections.<ArtifactType> emptyList());
   }
}
