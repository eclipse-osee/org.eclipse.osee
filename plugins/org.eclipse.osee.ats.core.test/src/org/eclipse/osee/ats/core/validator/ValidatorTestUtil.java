/*
 * Created on May 20, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import java.util.ArrayList;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class ValidatorTestUtil {

   public static MockValueProvider emptyValueProvider = new MockValueProvider(new ArrayList<String>());

   public static void assertValidResult(WidgetResult result) {
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());
      Assert.assertNull(result.getWidgetDef());
      Assert.assertEquals("", result.getDetails());
   }

}
