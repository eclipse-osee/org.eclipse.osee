/*
 * Created on Mar 29, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test case for {@link AtsCoreXWidgetValidatorProvider}
 *
 * @author Donald G. Dunne
 */
public class AtsCoreXWidgetValidatorProviderTest {

   @Test
   public void testGetValidators() {
      Assert.assertNotNull(AtsCoreXWidgetValidatorProvider.instance.getValidators());
      Assert.assertNotNull(AtsCoreXWidgetValidatorProvider.instance.getValidators());
   }

}
