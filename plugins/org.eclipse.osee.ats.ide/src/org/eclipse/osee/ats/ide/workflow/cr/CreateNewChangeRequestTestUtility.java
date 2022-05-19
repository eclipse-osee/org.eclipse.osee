/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr;

import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class CreateNewChangeRequestTestUtility {

   public static ActionResult testCreate(CreateNewChangeRequestBlam crBlam, final String title) {
      BlamEditor.edit(crBlam);
      BlamEditor blamEd = null;
      while (blamEd == null) {
         Collection<BlamEditor> editors = BlamEditor.getEditors();
         if (editors.isEmpty()) {
            continue;
         }
         blamEd = editors.iterator().next();
         if (blamEd == null) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ex) {
               // do nothing
            }
         }
      }
      final CreateNewChangeRequestBlam fBlam = (CreateNewChangeRequestBlam) blamEd.getEditorInput().getBlamOperation();
      fBlam.setOverrideTitle(title);
      final BlamEditor fBlamEd = blamEd;
      ActionResult actionResult = null;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            IAtsActionableItem programAi = null;
            int count = 0;
            while (programAi == null) {
               programAi = fBlam.getSelectedProgramAiOrSentinel();
               if (programAi == null) {
                  try {
                     Thread.sleep(1000);
                  } catch (InterruptedException ex) {
                     // do nothing
                  }
                  if (count < 5) {
                     count++;
                     System.err.println("count: " + count);
                  }
               }
            }
            fBlam.handlePopulateWithDebugInfo();
            try {
               Thread.sleep(5000);
            } catch (InterruptedException ex) {
               // do nothing
            }
            fBlamEd.executeBlam();
         }
      }, true);

      actionResult = fBlam.getActionResult();

      int count = 0;
      while (actionResult == null && count <= 10) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
         actionResult = fBlam.getActionResult();
      }
      // If this fails, create PR manually to see exception
      Assert.assertNotNull("Didn't get action results", actionResult);
      Assert.assertTrue(actionResult.getResults().toString(), actionResult.getResults().isSuccess());

      return actionResult;
   }

}
