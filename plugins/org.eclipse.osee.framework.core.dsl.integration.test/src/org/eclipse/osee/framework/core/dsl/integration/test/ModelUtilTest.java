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
package org.eclipse.osee.framework.core.dsl.integration.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.core.dsl.integration.ModelUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link ModelUtil}
 * 
 * @author Roberto E. Escobar
 */
public class ModelUtilTest {

   private static final String TYPE_TEST_INPUT = "testTypeModel.osee";
   private static final String ACCESS_TEST_INPUT = "testAccessModel.osee";

   private String getRawXTextData(String testInputFile) throws IOException {
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(this.getClass().getResourceAsStream(testInputFile));
         return Lib.inputStreamToString(inputStream);
      } finally {
         Lib.close(inputStream);
      }
   }

   @Test
   public void testModelUtilLoadType() throws OseeCoreException, IOException {
      String rawXTextData = getRawXTextData(TYPE_TEST_INPUT);

      OseeDsl model1 = ModelUtil.loadModel("osee:/text.osee", rawXTextData);
      Assert.assertEquals(5, model1.getArtifactTypes().size());
      Assert.assertEquals(3, model1.getAttributeTypes().size());
      Assert.assertEquals(1, model1.getRelationTypes().size());

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ModelUtil.saveModel(model1, "osee:/text.osee", outputStream, false);
      outputStream.flush();
      String value = outputStream.toString("UTF-8");

      OseeDsl model2 = ModelUtil.loadModel("osee:/text2.osee", value);

      Assert.assertEquals(model1.getArtifactTypes().size(), model2.getArtifactTypes().size());
      Assert.assertEquals(model1.getAttributeTypes().size(), model2.getAttributeTypes().size());
      Assert.assertEquals(model1.getRelationTypes().size(), model2.getRelationTypes().size());
      Assert.assertEquals(model1.getImports().size(), model2.getImports().size());
      Assert.assertEquals(model1.getEnumOverrides().size(), model2.getEnumOverrides().size());
      Assert.assertEquals(model1.getEnumTypes().size(), model2.getEnumTypes().size());
   }

   @Ignore
   @Test
   public void testModelUtilLoadAccess() throws OseeCoreException, IOException {
      String rawXTextData = getRawXTextData(ACCESS_TEST_INPUT);

      OseeDsl model1 = ModelUtil.loadModel("osee:/text.osee", rawXTextData);
      Assert.assertEquals(5, model1.getArtifactTypes().size());
      Assert.assertEquals(3, model1.getAttributeTypes().size());
      Assert.assertEquals(1, model1.getRelationTypes().size());

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ModelUtil.saveModel(model1, "osee:/text.osee", outputStream, false);
      outputStream.flush();
      String value = outputStream.toString("UTF-8");

      OseeDsl model2 = ModelUtil.loadModel("osee:/text2.osee", value);

      Assert.assertEquals(model1.getArtifactTypes().size(), model2.getArtifactTypes().size());
      Assert.assertEquals(model1.getAttributeTypes().size(), model2.getAttributeTypes().size());
      Assert.assertEquals(model1.getRelationTypes().size(), model2.getRelationTypes().size());
      Assert.assertEquals(model1.getImports().size(), model2.getImports().size());
      Assert.assertEquals(model1.getEnumOverrides().size(), model2.getEnumOverrides().size());
      Assert.assertEquals(model1.getEnumTypes().size(), model2.getEnumTypes().size());
   }

}
