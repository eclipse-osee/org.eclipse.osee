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

package org.eclipse.osee.ats.ide.integration.tests.framework.ui.skynet.dialog;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactTypeDialog;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class FilteredTreeArtifactTypeDialogTest {

   private static Collection<ArtifactTypeToken> artifactTypes;

   @Test
   public void test() {
      artifactTypes = Arrays.asList(CoreArtifactTypes.Artifact, CoreArtifactTypes.AbstractAccessControlled,
         CoreArtifactTypes.AbstractHeading, CoreArtifactTypes.AbstractImplementationDetails,
         CoreArtifactTypes.AbstractTestResult, CoreArtifactTypes.BranchView);
      FilteredTreeArtifactTypeDialog dialog = new FilteredTreeArtifactTypeDialog("title", "message", artifactTypes);
      dialog.setMultiSelect(false);
      dialog.setInput(artifactTypes);

      try {
         dialog.setBlockOnOpen(false);
         dialog.open();

         int count = dialog.getTreeViewer().getViewer().getTree().getItemCount();
         Assert.assertTrue(count >= 6);
      } finally {
         dialog.close();
      }
   }
}
