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
package org.eclipse.osee.ats.ide.ev;

import java.util.Collection;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.column.WorkPackageFilterTreeDialog;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.world.IWorldEditorProvider;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleSearchProvider;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleSearchProvider.IWorldEditorSimpleSearchProvider;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class WorkPackageQBDReport extends XNavigateItemAction implements IWorldEditorSimpleSearchProvider {

   public static final String TITLE = "Work Package QBD Report";
   private IAtsWorkPackage selectedWorkPackage;

   public WorkPackageQBDReport(XNavigateItem parent) {
      super(parent, TITLE, AtsImage.WORK_PACKAGE);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      WorkPackageFilterTreeDialog dialog =
         new WorkPackageFilterTreeDialog(getName(), "Select Work Package", new WorkPackageSearchProvider());
      dialog.setShowRemoveCheckbox(false);
      dialog.setInput();
      if (dialog.open() == Window.OK) {
         selectedWorkPackage = dialog.getSelection();
         WorldEditor.open(new WorldEditorSimpleSearchProvider(this));
      }
   }

   @Override
   public String getSearchName() {
      return TITLE + " for [" + selectedWorkPackage.getName() + "]";
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      List<ArtifactToken> workItemTokens = ArtifactQuery.getArtifactTokenListFromSoleAttributeInherited(
         AtsArtifactTypes.AbstractWorkflowArtifact, AtsAttributeTypes.WorkPackageReference,
         selectedWorkPackage.getIdString(), AtsClientService.get().getAtsBranch());
      List<Artifact> workItems =
         ArtifactQuery.getArtifactListFrom(workItemTokens, AtsClientService.get().getAtsBranch());
      return workItems;
   }

   @Override
   public IWorldEditorProvider copyProvider() {
      return new WorldEditorSimpleSearchProvider(this);
   }

   public void setSelectedWorkPackage(IAtsWorkPackage selectedWorkPackage) {
      this.selectedWorkPackage = selectedWorkPackage;
   }

}
