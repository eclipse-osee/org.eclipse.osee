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
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.UserCommunityListDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public class UserCommunitySearchItem extends WorldUISearchItem {

   private String userComm;
   private String selectedUserComm;
   private final String userCommName;

   public UserCommunitySearchItem(String displayName, String userCommName) throws OseeArgumentException {
      super(displayName, AtsImage.GLOBE);
      this.userCommName = userCommName;
   }

   public UserCommunitySearchItem() throws OseeArgumentException {
      this("User Community Search", null);
   }

   public UserCommunitySearchItem(UserCommunitySearchItem userCommunitySearchItem) {
      super(userCommunitySearchItem, AtsImage.GLOBE);
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

      return ArtifactQuery.getArtifactsFromTypeAndAttribute(TeamWorkFlowArtifact.ARTIFACT_NAME,
            ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getStoreName(), getSearchUserComm(), AtsPlugin.getAtsBranch());
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
         if (gld.getResult().length == 0) {
            AWorkbench.popup("ERROR", "No Group Selected");
            cancelled = true;
            return;
         }
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
