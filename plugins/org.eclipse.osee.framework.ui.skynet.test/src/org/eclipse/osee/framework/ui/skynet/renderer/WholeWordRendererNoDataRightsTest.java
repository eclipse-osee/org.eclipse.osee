/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.renderer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.ui.skynet.render.WholeWordRenderer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Regression test to ensure that {@link WholeWordRenderer} does not reference or use data rights functionality.
 * <p>
 * Whole word content publishes should not apply data rights because those documents already contain their own data
 * rights within the document. Data rights processing is only for word template content publishes.
 */

public class WholeWordRendererNoDataRightsTest {

   /**
    * Verifies that the WholeWordRenderer class does not declare any method named "addDataRights". This ensures that
    * data rights processing is not re-introduced into the whole word publish path.
    */

   @Test
   public void testNoAddDataRightsMethod() {
      Method[] methods = WholeWordRenderer.class.getDeclaredMethods();
      List<String> dataRightsMethods =
         Arrays.stream(methods).map(Method::getName).filter(name -> name.toLowerCase(Locale.ROOT).contains(
            "datarights") || name.toLowerCase().contains("data_rights")).collect(Collectors.toList());

      Assert.assertTrue("WholeWordRenderer should not contain any data rights methods, but found: " + dataRightsMethods,
         dataRightsMethods.isEmpty());
   }

   /**
    * Verifies that the WholeWordRenderer class does not declare any field related to data rights. This ensures that
    * data rights infrastructure (patterns, builders, etc.) is not re-introduced.
    */

   @Test
   public void testNoDataRightsFields() {
      Field[] fields = WholeWordRenderer.class.getDeclaredFields();
      List<String> dataRightsFields = Arrays.stream(fields).map(Field::getName).filter(
         name -> name.toLowerCase().contains("datarights") || name.toLowerCase(Locale.ROOT).contains(
            "data_rights") || name.toLowerCase().contains("classification")).collect(Collectors.toList());

      Assert.assertTrue("WholeWordRenderer should not contain any data rights fields, but found: " + dataRightsFields,
         dataRightsFields.isEmpty());
   }

   /**
    * Verifies that the WholeWordRenderer class does not import any data rights related classes. This is checked by
    * verifying that the class does not reference DataRightsClassification or DataRightContentBuilder types in its
    * declared fields or method parameters.
    */

   @Test
   public void testNoDataRightsTypeReferences() {
      // Check all declared fields for data rights types
      Field[] fields = WholeWordRenderer.class.getDeclaredFields();
      for (Field field : fields) {
         String typeName = field.getType().getName();
         Assert.assertFalse(
            "WholeWordRenderer field '" + field.getName() + "' references data rights type: " + typeName,
            typeName.contains("DataRight") || typeName.contains("DataRightsClassification"));
      }

      // Check all declared methods for data rights return types or parameter types
      Method[] methods = WholeWordRenderer.class.getDeclaredMethods();
      for (Method method : methods) {
         String returnTypeName = method.getReturnType().getName();
         Assert.assertFalse(
            "WholeWordRenderer method '" + method.getName() + "' returns data rights type: " + returnTypeName,
            returnTypeName.contains("DataRight"));

         for (Class<?> paramType : method.getParameterTypes()) {
            Assert.assertFalse(
               "WholeWordRenderer method '" + method.getName() + "' has data rights parameter type: " + paramType.getName(),
               paramType.getName().contains("DataRight"));
         }
      }
   }
}

/* EOF */
