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

package org.eclipse.osee.ats.ide.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;

/**
 * @author Vaibhav Patel
 */
public class EditEnumeratedArtifact extends XNavigateItemAction {

   private static final String TITLE = "Edit Enumerated Artifact";

   public EditEnumeratedArtifact() {
      super(TITLE, FrameworkImage.GEAR, XNavigateItem.DEFINE_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      List<Artifact> arts = new ArrayList<>();
      arts.addAll(
         Collections.castAll(AtsApiService.get().getQueryService().getArtifacts(CoreArtifactTypes.OseeTypeEnum)));
      if (arts.isEmpty()) {
         AWorkbench.popup("Warning", "No Enumerated Artifact(s) Found.");
      } else {
         FilteredTreeDialog dialog = new FilteredTreeDialog(TITLE, "Select Enumerated Artifact to edit.",
            new ArrayTreeContentProvider(), new AtsObjectLabelProvider(false, true));
         dialog.setInput(arts);
         dialog.setMultiSelect(false);
         if (dialog.open() == Window.OK) {
            Artifact selectedArt = dialog.getSelectedFirst();
            ArtifactEditor.editArtifact(selectedArt);
         }
      }
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN, XNavItemCat.OSEE_ADMIN);
   }
}