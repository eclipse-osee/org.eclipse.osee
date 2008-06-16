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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ArtIdSearchItem extends WorldSearchItem {
   private String enteredIds = "";

   public ArtIdSearchItem() {
      super("Search by Guid/ArtId on Default Branch");
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      for (Artifact art : ArtifactQuery.getArtifactsFromIds(Lib.stringToIntegerList(enteredIds),
            BranchPersistenceManager.getInstance().getDefaultBranch(), false)) {
         artifacts.add(art);
      }
      for (Artifact art : ArtifactQuery.getArtifactsFromIds(Arrays.asList(enteredIds.split(",")),
            BranchPersistenceManager.getInstance().getDefaultBranch())) {
         artifacts.add(art);
      }
      if (artifacts.size() == 0) {
         AWorkbench.popup(
               "ERROR",
               "Didn't find any artifacts on default branch \"" + BranchPersistenceManager.getInstance().getDefaultBranch() + "\"");
      }
      return artifacts;
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException, SQLException {
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

}
