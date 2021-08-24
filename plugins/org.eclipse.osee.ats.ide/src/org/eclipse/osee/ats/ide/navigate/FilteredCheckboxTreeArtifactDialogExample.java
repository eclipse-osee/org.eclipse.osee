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

package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeChildrenContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class FilteredCheckboxTreeArtifactDialogExample extends XNavigateItemAction {

   public FilteredCheckboxTreeArtifactDialogExample() {
      super("FilteredCheckboxTreeArtifactDialog Example", FrameworkImage.EXAMPLE, XNavigateItem.UTILITY_EXAMPLES);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(AtsApiService.get().getAtsBranch());
      List<Artifact> initialSelection = new ArrayList<>();
      List<Artifact> children = rootArtifact.getChildren();
      initialSelection.add(children.iterator().next());
      FilteredCheckboxTreeArtifactDialog dialog = new FilteredCheckboxTreeArtifactDialog("My Title", "Message",
         children, new ArtifactTreeChildrenContentProvider(Artifact.class), new ArtifactLabelProvider());
      dialog.setInitialSelections(Arrays.asList(children.get(1), children.get(2)));
      dialog.setExpandChecked(true);
      FilteredDialogExampleUtil.openAndReport(dialog, getName());
   }

}
