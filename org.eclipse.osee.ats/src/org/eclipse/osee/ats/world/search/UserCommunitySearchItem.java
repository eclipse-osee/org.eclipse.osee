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
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.util.widgets.dialog.UserCommunityListDialog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * @author Donald G. Dunne
 */
public class UserCommunitySearchItem extends WorldSearchItem {

   private String userComm;
   private String selectedUserComm;
   private final String userCommName;

   public UserCommunitySearchItem(String displayName, String userCommName) {
      super(displayName);
      this.userCommName = userCommName;
   }

   public UserCommunitySearchItem() {
      this("User Community Search", null);
   }

   public String getGroupSearchName() {
      if (userComm != null)
         return userComm;
      else if (selectedUserComm != null) return selectedUserComm;
      return "";
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return String.format("%s - %s", super.getSelectedName(searchType), getGroupSearchName());
   }

   private String getSearchUserComm() {
      if (userComm != null) return userComm;
      return selectedUserComm;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {
      if (isCancelled()) return EMPTY_SET;

      // Find all Team Workflows artifact types
      List<ISearchPrimitive> teamWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (String teamArtName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         teamWorkflowCriteria.add(new ArtifactTypeSearch(teamArtName, Operator.EQUAL));
      FromArtifactsSearch teamWorkflowSearch = new FromArtifactsSearch(teamWorkflowCriteria, false);

      List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new AttributeValueSearch(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName(), getSearchUserComm(),
            Operator.EQUAL));
      criteria.add(teamWorkflowSearch);
      FromArtifactsSearch criteriaSearch = new FromArtifactsSearch(criteria, true);

      List<ISearchPrimitive> actionCriteria = new LinkedList<ISearchPrimitive>();
      actionCriteria.add(new InRelationSearch(criteriaSearch, RelationSide.ActionToWorkflow_Action));

      Collection<Artifact> arts =
            ArtifactPersistenceManager.getInstance().getArtifacts(actionCriteria, true,
                  BranchPersistenceManager.getInstance().getAtsBranch());

      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

   @Override
   public void performUI(SearchType searchType) {
      super.performUI(searchType);
      if (userCommName != null) return;
      if (userComm != null) return;
      if (searchType == SearchType.ReSearch && selectedUserComm != null) return;
      UserCommunityListDialog gld = new UserCommunityListDialog();
      int result = gld.open();
      if (result == 0) {
         selectedUserComm = (String) gld.getResult()[0];
         return;
      } else {
         selectedUserComm = null;
         cancelled = true;
      }
   }

}
