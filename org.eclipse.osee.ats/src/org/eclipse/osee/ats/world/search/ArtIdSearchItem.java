/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.world.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ArtIdSearchItem extends WorldUISearchItem {
   private String enteredIds = "";

   public ArtIdSearchItem() {
      super("Search by Guid/ArtId on Default Branch");
   }

   public ArtIdSearchItem(ArtIdSearchItem artIdSearchItem) {
      super(artIdSearchItem);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      for (Artifact art : ArtifactQuery.getArtifactsFromIds(Lib.stringToIntegerList(enteredIds),
            BranchManager.getDefaultBranch(), false)) {
         artifacts.add(art);
      }
      for (Artifact art : ArtifactQuery.getArtifactsFromIds(Arrays.asList(enteredIds.split(",")),
            BranchManager.getDefaultBranch())) {
         artifacts.add(art);
      }
      if (artifacts.size() == 0) {
         AWorkbench.popup("ERROR",
               "Didn't find any artifacts on default branch \"" + BranchManager.getDefaultBranch() + "\"");
      }
      return artifacts;
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
      super.performUI(searchType);
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), getName(), null,
                  "Enter Guid(s)/ArtId(s) (comma separated)", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      int response = ed.open();
      if (response == 0) {
         enteredIds = ed.getEntry();
         enteredIds = enteredIds.replaceAll(" ", "");
         return;
      } else
         enteredIds = null;
      cancelled = true;
   }

   /**
    * @return the enteredIds
    */
   public String getEnteredIds() {
      return enteredIds;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new ArtIdSearchItem(this);
   }

}
