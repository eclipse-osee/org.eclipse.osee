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
package org.eclipse.osee.framework.core.test.fields;

import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.fields.AssociatedArtifactField;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case For {@link AssociatedArtifactField}
 * 
 * @author Roberto E. Escobar
 */
public class AssociatedArtifactFieldTest {

   private static IBasicArtifact<?> art1;
   private static IBasicArtifact<?> art2;

   //   private static IBasicArtifact<?> art3;

   @BeforeClass
   public static void prepareTest() {
      art1 = MockDataFactory.createArtifact(1);
      art2 = MockDataFactory.createArtifact(2);
      //      art3 = MockDataFactory.createArtifact(3);
   }

   @Test
   public void test() throws OseeCoreException {
      AssociatedArtifactField field = new AssociatedArtifactField(art1);
      Assert.assertEquals(false, field.isDirty());

      field.set(art2);
      Assert.assertEquals(true, field.isDirty());
      field.clearDirty();

   }
}
