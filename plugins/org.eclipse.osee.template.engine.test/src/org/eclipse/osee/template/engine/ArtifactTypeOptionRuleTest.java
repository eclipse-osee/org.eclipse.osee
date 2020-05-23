/*********************************************************************
 * Copyright (c) 2014 Boeing
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