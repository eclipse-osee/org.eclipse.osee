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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactIdSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ArtIdSearchItem extends WorldSearchItem {
   private String enteredIds = "";

   public ArtIdSearchItem() {
      super("Search by ArtId(s)");
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {

      List<ISearchPrimitive> idCriteria = new LinkedList<ISearchPrimitive>();
      for (String str : enteredIds.split(",")) {
         str = str.replaceAll("\\s+", "");
         try {
            int id = (new Integer(str)).intValue();
            idCriteria.add(new ArtifactIdSearch(id, Operator.EQUAL));
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
         }
      }

      if (isCancelled()) return EMPTY_SET;

      if (idCriteria.size() > 0) {
         Collection<Artifact> artifacts =
               ArtifactPersistenceManager.getInstance().getArtifacts(idCriteria, false,
                     BranchPersistenceManager.getInstance().getAtsBranch());
         ArtifactEditor.editArtifacts(artifacts);
         return artifacts;
      }

      return EMPTY_SET;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), getName(), null, "Enter ArtId(s) (comma separated)",
                  MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
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
