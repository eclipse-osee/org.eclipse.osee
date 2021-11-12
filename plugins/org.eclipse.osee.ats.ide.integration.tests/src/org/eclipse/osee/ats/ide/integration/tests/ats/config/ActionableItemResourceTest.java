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

package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
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
      testActionableItemUrl("/ats/ai", 71, false);
   }

   @Test
   public void testAtsAisDetailsRestCall() {
      testActionableItemUrl("/ats/ai/details", 71, true);
   }

   @Test
   public void testAtsAiRestCall() {
      testUrl("/ats/ai/" + getSawCodeAi().getIdString(), "SAW Code");
   }

   @Test
   public void testAtsAiDetailsRestCall() {
      testActionableItemUrl("/ats/ai/" + getSawCodeAi().getIdString() + "/details", 1, true);
   }

   private Artifact getSawCodeAi() {
      return ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.ActionableItem, "SAW Code",
         AtsApiService.get().getAtsBranch());
   }
}