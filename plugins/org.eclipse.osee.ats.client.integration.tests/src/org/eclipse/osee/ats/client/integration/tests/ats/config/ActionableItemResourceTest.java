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
package org.eclipse.osee.ats.client.integration.tests.ats.config;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Test;

/**
 * Unit Test for {@link ActionableItemResource}
 *
 * @author Donald G. Dunne
 */
public class ActionableItemResourceTest extends AbstractRestTest {

   private void testActionableItemUrl(String url, int size, boolean isActive) {
      testUrl(url, size, "SAW Code", "ats.Active", isActive);
   }

   @Test
   public void testAtsAisRestCall() {
      testActionableItemUrl("/ats/ai", 46, false);
   }

   @Test
   public void testAtsAisDetailsRestCall() {
      testActionableItemUrl("/ats/ai/details", 46, true);
   }

   @Test
   public void testAtsAiRestCall() {
      testActionableItemUrl("/ats/ai/" + getSawCodeAi().getArtId(), 1, false);
   }

   @Test
   public void testAtsAiDetailsRestCall() {
      testActionableItemUrl("/ats/ai/" + getSawCodeAi().getArtId() + "/details", 1, true);
   }

   private Artifact getSawCodeAi() {
      return ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.ActionableItem, "SAW Code",
         AtsClientService.get().getAtsBranch());
   }
}