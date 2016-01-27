/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.config;

import java.util.Arrays;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ActionableItemArtifactReader}
 *
 * @author Donald G. Dunne
 */
public class ActionableItemArtifactReaderTest {

   @Test
   public void testLoad() {
      IAtsActionableItem sawTestAi = AtsClientService.get().getConfig().getSoleByUuid(
         DemoArtifactToken.SAW_Test_AI.getUuid(), IAtsActionableItem.class);
      Assert.assertTrue(sawTestAi.isAllowUserActionCreation());
      Assert.assertEquals(1, ActionableItems.getUserEditableActionableItems(Arrays.asList(sawTestAi)).size());

      sawTestAi.setAllowUserActionCreation(false);
      AtsChangeSet changes = new AtsChangeSet(getClass().getSimpleName());
      AtsClientService.get().storeConfigObject(sawTestAi, changes);
      changes.execute();

      sawTestAi = AtsClientService.get().getConfig().getSoleByUuid(DemoArtifactToken.SAW_Test_AI.getUuid(),
         IAtsActionableItem.class);
      Assert.assertFalse(sawTestAi.isAllowUserActionCreation());
      Assert.assertEquals(0, ActionableItems.getUserEditableActionableItems(Arrays.asList(sawTestAi)).size());

      sawTestAi.setAllowUserActionCreation(true);
      changes = new AtsChangeSet(getClass().getSimpleName());
      AtsClientService.get().storeConfigObject(sawTestAi, changes);
      changes.execute();

      sawTestAi = AtsClientService.get().getConfig().getSoleByUuid(DemoArtifactToken.SAW_Test_AI.getUuid(),
         IAtsActionableItem.class);
      Assert.assertTrue(sawTestAi.isAllowUserActionCreation());
      Assert.assertEquals(1, ActionableItems.getUserEditableActionableItems(Arrays.asList(sawTestAi)).size());

   }

}
