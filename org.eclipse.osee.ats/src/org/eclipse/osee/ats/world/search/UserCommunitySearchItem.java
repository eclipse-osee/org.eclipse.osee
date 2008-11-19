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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.dialog.UserCommunityListDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.DepricatedOperator;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;

/**
 * @author Donald G. Dunne
 */
public class UserCommunitySearchItem extends WorldUISearchItem {

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

   public UserCommunitySearchItem(UserCommunitySearchItem userCommunitySearchItem) {
      super(userCommunitySearchItem);
      this.userComm = userCommunitySearchItem.userComm;
      this.userCommName = userCommunitySearchItem.userCommName;
   }

   public String getGroupSearchName() {
      if (userComm != null)
         return userComm;
      else if (selectedUserComm != null) return selectedUserComm;
      return "";
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("%s - %s", super.getSelectedName(searchType), getGroupSearchName());
   }

   private String getSearchUserComm() {
      if (userComm != null) return userComm;
      return selectedUserComm;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      if (isCancelled()) return EMPTY_SET;

      // Find all Team Workflows artifact types
      List<ISearchPrimitive> teamWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (String teamArtName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames()) {
         teamWorkflowCriteria.add(new ArtifactTypeSearch(teamArtName, DepricatedOperator.EQUAL));
      }
      FromArtifactsSearch teamWorkflowSearch = new FromArtifactsSearch(teamWorkflowCriteria, false);

      List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      criteria.add(new AttributeValueSearch(ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName(), getSearchUserComm(),
            DepricatedOperator.EQUAL));
      criteria.add(teamWorkflowSearch);
      FromArtifactsSearch criteriaSearch = new FromArtifactsSearch(criteria, true);

      List<ISearchPrimitive> actionCriteria = new LinkedList<ISearchPrimitive>();
      actionCriteria.add(new InRelationSearch(criteriaSearch, AtsRelation.ActionToWorkflow_Action));

      Collection<Artifact> arts =
            ArtifactPersistenceManager.getArtifacts(actionCriteria, true, AtsPlugin.getAtsBranch());

      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
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

   /**
    * @param selectedUserComm the selectedUserComm to set
    */
   public void setSelectedUserComm(String selectedUserComm) {
      this.selectedUserComm = selectedUserComm;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new UserCommunitySearchItem(this);
   }

}
