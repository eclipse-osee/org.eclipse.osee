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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeSearchItem extends WorldSearchItem {

   private String searchStr;
   private final String attributeName;

   public AtsAttributeSearchItem(String searchName, String attributeName, String searchStr) {
      super(searchName);
      this.attributeName = attributeName;
      this.searchStr = searchStr;
   }

   public AtsAttributeSearchItem(String searchName, String searchStr) {
      this("%", searchName, searchStr);
   }

   public AtsAttributeSearchItem() {
      this("Search ATS Attributes", "%", "");
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return String.format("%s - %s", super.getSelectedName(searchType), searchStr);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {
      if (searchStr == null) return EMPTY_SET;
      LinkedList<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new AttributeValueSearch(attributeName, searchStr, Operator.CONTAINS));
      FromArtifactsSearch stringCriteria = new FromArtifactsSearch(criteria, true);

      LinkedList<ISearchPrimitive> atsObjectCriteria = new LinkedList<ISearchPrimitive>();
      atsObjectCriteria.add(new ArtifactTypeSearch(ActionArtifact.ARTIFACT_NAME, Operator.EQUAL));
      for (String teamArtifactName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         atsObjectCriteria.add(new ArtifactTypeSearch(teamArtifactName, Operator.EQUAL));
      atsObjectCriteria.add(new ArtifactTypeSearch(TaskArtifact.ARTIFACT_NAME, Operator.EQUAL));
      FromArtifactsSearch atsObjectSearch = new FromArtifactsSearch(atsObjectCriteria, false);

      LinkedList<ISearchPrimitive> bothCriteria = new LinkedList<ISearchPrimitive>();
      bothCriteria.add(stringCriteria);
      bothCriteria.add(atsObjectSearch);

      if (cancelled) return EMPTY_SET;
      Collection<Artifact> artifacts =
            ArtifactPersistenceManager.getInstance().getArtifacts(bothCriteria, true,
                  BranchPersistenceManager.getInstance().getAtsBranch());
      if (cancelled) return EMPTY_SET;
      return artifacts;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Search by Ats Attribute", null,
                  "Enter string to search for.", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      if (ed.open() == 0) {
         searchStr = ed.getEntry();
         return;
      } else
         cancelled = true;
   }

}
