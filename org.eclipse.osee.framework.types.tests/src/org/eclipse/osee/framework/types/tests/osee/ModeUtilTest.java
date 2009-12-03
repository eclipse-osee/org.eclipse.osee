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
package org.eclipse.osee.framework.types.tests.osee;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.osee.ModelUtil;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class ModeUtilTest {

   private static String rawXTextData = //
         "artifactType \"Artifact\" { \n" + //
         "     guid \"AAMFDh6S7gRLupAMwywA\" \n" + //
         "     attribute \"Name\" \n" + //
         "     attribute \"Annotation\" \n" + //
         "}\n" + //
         "attributeType \"Name\" extends StringAttribute {\n" + //
         "     guid \"AAMFEcF1AzV7PKuHmxwA\" \n" + //
         "     dataProvider DefaultAttributeDataProvider\n" + //
         "     min 1\n" + //
         "     max 1\n" + //
         "     taggerId DefaultAttributeTaggerProvider\n" + //
         "     description \"Descriptive Name\"\n" + //
         "     defaultValue \"unnamed\"\n" + //
         "}\n" + //
         "attributeType \"Annotation\" extends StringAttribute {\n" + //
         "guid \"AAMFEcWy0xc4e3tcemQA\" \n" + //
         "dataProvider DefaultAttributeDataProvider\n" + //
         "min 0\n" + //
         "max unlimited\n" + //
         "taggerId DefaultAttributeTaggerProvider\n" + //
         "}\n" + //
         "\n";

   @Test
   public void testModelUtilSave() throws OseeCoreException, IOException, URISyntaxException {
      OseeTypeModel model1 = ModelUtil.loadModel("osee:/text.osee", rawXTextData);
      Assert.assertEquals(1, model1.getArtifactTypes().size());
      Assert.assertEquals(2, model1.getAttributeTypes().size());

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ModelUtil.saveModel(model1, "osee:/text.osee", outputStream, false);
      outputStream.flush();
      String value = outputStream.toString("UTF-8");

      OseeTypeModel model2 = ModelUtil.loadModel("osee:/text2.osee", value);

      Assert.assertEquals(model1.getArtifactTypes().size(), model2.getArtifactTypes().size());
      Assert.assertEquals(model1.getAttributeTypes().size(), model2.getAttributeTypes().size());
      Assert.assertEquals(model1.getRelationTypes().size(), model2.getRelationTypes().size());
      Assert.assertEquals(model1.getImports().size(), model2.getImports().size());
      Assert.assertEquals(model1.getEnumOverrides().size(), model2.getEnumOverrides().size());
      Assert.assertEquals(model1.getEnumTypes().size(), model2.getEnumTypes().size());
   }

}
