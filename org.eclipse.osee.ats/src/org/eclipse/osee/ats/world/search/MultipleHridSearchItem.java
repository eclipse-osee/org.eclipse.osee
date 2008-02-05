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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.LegacyPCRActions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactGuidSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactHridSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class MultipleHridSearchItem extends WorldSearchItem {
   private String enteredIds = "";

   public MultipleHridSearchItem() {
      super("Search by ID(s)");
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {

      List<ISearchPrimitive> idCriteria = new LinkedList<ISearchPrimitive>();
      Set<String> nonHridGuids = new HashSet<String>();
      for (String str : enteredIds.split(",")) {
         str = str.replaceAll(" ", "");
         if (str.length() == 5)
            idCriteria.add(new ArtifactHridSearch(str));
         else if (GUID.isValid(str))
            idCriteria.add(new ArtifactGuidSearch(str));
         else {
            nonHridGuids.add(str);
         }
      }

      if (isCancelled()) return EMPTY_SET;

      Collection<Artifact> resultArts = new HashSet<Artifact>();

      // If items were entered that are not HRIDs or Guids, attempt to open via legacy pcr field
      if (nonHridGuids.size() > 0) {
         Collection<ActionArtifact> actionArts =
               LegacyPCRActions.getTeamsActionArtifacts(nonHridGuids, (Collection<TeamDefinitionArtifact>) null);
         if (actionArts.size() == 0) {
            // Attempt to open off legacy PCR field
            OSEELog.logException(AtsPlugin.class,
                  "Invalid HRID/Guid/Legacy PCR Id(s): " + AtsLib.commaStrs(nonHridGuids), null, true);
         } else {
            for (ActionArtifact teamWf : actionArts) {
               resultArts.add(teamWf);
            }
         }
      }

      if (idCriteria.size() > 0) {
         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(idCriteria, false,
                     BranchPersistenceManager.getInstance().getAtsBranch());
         if (isCancelled()) return EMPTY_SET;
         if (arts != null) resultArts.addAll(arts);
      }

      return resultArts;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), getName(), null,
                  "Enter GUID(s) or 5 Character ID(s) (comma separated)", MessageDialog.QUESTION, new String[] {"OK",
                        "Cancel"}, 0);
      int response = ed.open();
      if (response == 0) {
         enteredIds = ed.getEntry();
         if (enteredIds.equals("oseerocks")) {
            AWorkbench.popup("Confirmation", "Confirmed!  Osee Rocks!");
            cancelled = true;
            return;
         }
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
