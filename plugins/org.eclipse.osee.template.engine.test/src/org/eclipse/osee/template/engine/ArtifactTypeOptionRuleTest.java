/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.template.engine;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * Unit Test for {@link ArtifactTypeOptionRule}
 * 
 * @author Marc Potter
 */
public class ArtifactTypeOptionRuleTest {

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testBuild() throws Exception {
      Set<String> typeNames = new HashSet<>();
      typeNames.add("Software Requirement Function");
      ArtifactTypeOptionsRule rule = new ArtifactTypeOptionsRule("select_id", typeNames, typeNames);
      StringBuilder builder = new StringBuilder();
      rule.applyTo(builder);
      String result = builder.toString();
      Assert.assertTrue(result.contains("typeSelected"));
      Assert.assertTrue(result.contains("typeDeselected"));
      Assert.assertTrue(result.contains("<script"));
      Assert.assertTrue(result.contains("</script>"));
   }
}