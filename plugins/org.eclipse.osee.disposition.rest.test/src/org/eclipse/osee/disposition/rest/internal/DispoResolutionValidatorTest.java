/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import static org.eclipse.osee.disposition.model.DispoStrings.ANALYZE_CODE;
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
      annotation.setResolutionType(ANALYZE_CODE);
      annotation.setResolution("");

      DispoResolutionValidator validator = new DispoResolutionValidator();

      validator.validate(annotation);

      Assert.assertTrue(annotation.getIsAnalyze());
      Assert.assertTrue(!annotation.getIsResolutionValid());

      annotation.setResolution("something");
      validator.validate(annotation);

      Assert.assertTrue(annotation.getIsAnalyze());
      Assert.assertTrue(annotation.getIsResolutionValid());

      annotation.setResolutionType("non-essense");
      validator.validate(annotation);

      Assert.assertTrue(!annotation.getIsAnalyze());
      Assert.assertTrue(annotation.getIsResolutionValid());

   }

}
