/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow.cr;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.ide.demo.workflow.cr.CreateNewDemoChangeRequestBlam;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.junit.Assert;
import org.junit.Before;

/**
 * This test will launch the Demo Change Request Blam, handlePopulateWithDebugInfo, run the BLAM and then check that the
 * change request workflow was created.
 *
 * @author Donald G. Dunne
 */
public class CreateNewDemoChangeRequestTest {

   AtsApi atsApi;
   public static String TITLE = "New CR - CreateNewDemoChangeRequestTest";

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      BlamEditor.closeAll();
   }

   @org.junit.Test
   public void testCreate() {

      CreateNewDemoChangeRequestBlam blam = new CreateNewDemoChangeRequestBlam();
      BlamEditor.edit(blam);
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
      final CreateNewDemoChangeRequestBlam fBlam =
         (CreateNewDemoChangeRequestBlam) blamEd.getEditorInput().getBlamOperation();
      final BlamEditor fBlamEd = blamEd;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            fBlam.handlePopulateWithDebugInfo(TITLE);
            fBlamEd.executeBlam();
         }
      }, true);

      ActionResult actionResult = fBlam.getActionResult();
      while (actionResult == null) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ex) {
            // do nothing
         }
         actionResult = fBlam.getActionResult();
      }
      Assert.assertTrue(actionResult.getResults().isSuccess());

      ArtifactToken artifactByName =
         atsApi.getQueryService().getArtifactByName(AtsDemoOseeTypes.DemoChangeRequestTeamWorkflow, TITLE);
      Assert.assertNotNull(artifactByName);
   }

}
