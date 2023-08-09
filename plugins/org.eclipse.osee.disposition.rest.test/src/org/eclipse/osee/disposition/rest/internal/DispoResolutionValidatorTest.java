/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.disposition.rest.internal;

import static org.eclipse.osee.disposition.model.DispoStrings.ANALYSIS;
import static org.eclipse.osee.disposition.model.DispoStrings.MODIFY_CODE;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class DispoResolutionValidatorTest {

   @Before
   public void setUp() {
   }

   @Test
   public void testValidate() {
      DispoAnnotationData annotation = new DispoAnnotationData();
      annotation.setResolutionType(MODIFY_CODE);
      annotation.setResolution("");

      DispoResolutionValidator validator = new DispoResolutionValidator();

      validator.validate(annotation);

      Assert.assertFalse(annotation.getIsAnalyze());
      Assert.assertTrue(annotation.getNeedsModify());
      Assert.assertFalse(annotation.getIsResolutionValid());

      annotation.setResolution("something");
      validator.validate(annotation);

      Assert.assertFalse(annotation.getIsAnalyze());
      Assert.assertTrue(annotation.getNeedsModify());
      Assert.assertTrue(annotation.getIsResolutionValid());

      annotation.setResolutionType(ANALYSIS);
      validator.validate(annotation);

      Assert.assertTrue(annotation.getIsAnalyze());
      Assert.assertFalse(annotation.getNeedsModify());
      Assert.assertTrue(annotation.getIsResolutionValid());

   }

}
