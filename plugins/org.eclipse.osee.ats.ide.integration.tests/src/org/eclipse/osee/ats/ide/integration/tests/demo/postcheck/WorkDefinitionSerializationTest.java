/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.demo.postcheck;

import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.junit.Assert;
import org.junit.Test;

/** Verifies every deployed Work Definition round-trips through the OSEE JAX-RS providers. */
public class WorkDefinitionSerializationTest {

   @Test
   public void workDefinitionsRoundTripThroughJaxRsJson() {
      Collection<WorkDefinition> workDefinitions = AtsApiService.get().getServerEndpoints().getWorkDefEp().get();
      Assert.assertFalse("Expected at least one Work Definition", workDefinitions.isEmpty());

      for (WorkDefinition workDefinition : workDefinitions) {
         Assert.assertNotNull("JAX-RS returned a null Work Definition", workDefinition);
         Assert.assertTrue("JAX-RS returned an invalid Work Definition", workDefinition.isValid());
      }
   }
}
