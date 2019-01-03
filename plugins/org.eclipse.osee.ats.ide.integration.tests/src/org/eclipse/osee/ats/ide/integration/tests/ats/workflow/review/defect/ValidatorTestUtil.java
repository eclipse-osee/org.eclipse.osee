/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.review.defect;

import java.util.ArrayList;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.core.util.StringValueProvider;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class ValidatorTestUtil {

   public static StringValueProvider emptyValueProvider = new StringValueProvider(new ArrayList<String>());

   public static void assertValidResult(WidgetResult result) {
      Assert.assertEquals(WidgetStatus.Valid, result.getStatus());
      Assert.assertNull(result.getWidgetDef());
      Assert.assertEquals(WidgetStatus.Valid.name(), result.getDetails());
   }

}
