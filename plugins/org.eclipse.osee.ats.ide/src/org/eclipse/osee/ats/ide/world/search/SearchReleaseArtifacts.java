/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;

/**
 * @author Vaibhav Patel
 */
public class SearchReleaseArtifacts extends XNavigateItemAction {

   public SearchReleaseArtifacts() {
      super("Search Release Artifacts", AtsImage.RELEASED, AtsNavigateViewItems.ATS_RELEASES);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      List<Artifact> arts = new ArrayList<>();
      arts.addAll(
         Collections.castAll(AtsApiService.get().getQueryService().getArtifacts(AtsArtifactTypes.ReleaseArtifact)));
      if (arts.isEmpty()) {
         AWorkbench.popup("Warning", "No Release Artifacts Found.");
      } else {
         MassArtifactEditor.editArtifacts(getName(), arts);
      }
   }
}