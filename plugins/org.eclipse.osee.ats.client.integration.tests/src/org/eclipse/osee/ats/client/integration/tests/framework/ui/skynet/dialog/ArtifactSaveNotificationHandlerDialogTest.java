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
package org.eclipse.osee.ats.client.integration.tests.framework.ui.skynet.dialog;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactSaveNotificationHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactSaveNotificationHandlerDialogTest {

   @Test
   public void test() {
      ArtifactSaveNotificationHandler handler = new ArtifactSaveNotificationHandler();
      List<Artifact> artifacts = new ArrayList<Artifact>();
      artifacts.add(DemoUtil.getSawCodeCommittedWf());
      artifacts.add(DemoUtil.getSawCodeUnCommittedWf());
      FilteredCheckboxTreeDialog dialog = handler.createDialog(artifacts);
      try {
         dialog.setBlockOnOpen(false);
         dialog.open();

         int count = dialog.getTreeViewer().getViewer().getTree().getItemCount();
         Assert.assertTrue(count == 2);
      } finally {
         dialog.close();
      }
   }
}
