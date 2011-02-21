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
package org.eclipse.osee.framework.core.dsl.integration.test.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.DslAsserts;
import org.eclipse.osee.framework.core.dsl.integration.util.ModelUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
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
      DslAsserts.assertEquals(model1, model2);
   }

   @Test
   public void testModelUtilLoadAccess() throws OseeCoreException, IOException {
      String rawXTextData = getRawXTextData(ACCESS_TEST_INPUT);

      OseeDsl model1 = ModelUtil.loadModel("osee:/text.osee", rawXTextData);
      Assert.assertEquals(2, model1.getArtifactTypes().size());
      Assert.assertEquals(1, model1.getAttributeTypes().size());
      Assert.assertEquals(0, model1.getRelationTypes().size());

      Assert.assertEquals(3, model1.getArtifactRefs().size());
      Assert.assertEquals(2, model1.getAccessDeclarations().size());

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ModelUtil.saveModel(model1, "osee:/text.osee", outputStream, false);
      outputStream.flush();
      String value = outputStream.toString("UTF-8");

      OseeDsl model2 = ModelUtil.loadModel("osee:/text2.osee", value);
      DslAsserts.assertEquals(model1, model2);

      //      String modelRep =
      //         ModelUtil.modelToStringXText(model1, "osee:/text.osee", Collections.<String, Boolean> emptyMap());
      //      OseeDsl model3 = ModelUtil.loadModel("osee:/text3.osee", modelRep);
      //      DslAsserts.assertEquals(model1, model3);
   }
}
