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
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.DepricatedOperator;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

/**
 * @author Donald G. Dunne
 */
public class MyTeamWFSearchItem extends UserSearchItem {

   public MyTeamWFSearchItem(String name) {
      this(name, null);
   }

   public MyTeamWFSearchItem() {
      super("My World", null);
   }

   public MyTeamWFSearchItem(String name, User user) {
      super(name, user);
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws Exception {

      // SMA having user as portion of current state attribute (Team WorkFlow and Task)
      List<ISearchPrimitive> currentStateCriteria = new LinkedList<ISearchPrimitive>();
      currentStateCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
            "<" + user.getUserId() + ">", DepricatedOperator.CONTAINS));
      FromArtifactsSearch currentStateSearch = new FromArtifactsSearch(currentStateCriteria, false);

      // Add all the Team Workflow's related to assigned Tasks
      List<ISearchPrimitive> teamAndTaskCriteria = new LinkedList<ISearchPrimitive>();
      teamAndTaskCriteria.add(currentStateSearch);
      teamAndTaskCriteria.add(new InRelationSearch(currentStateSearch, CoreRelationEnumeration.SmaToTask_Sma));
      FromArtifactsSearch teamAndTaskSearch = new FromArtifactsSearch(teamAndTaskCriteria, false);

      // Find all Team Workflows artifact types
      List<ISearchPrimitive> teamWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (String teamArtName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         teamWorkflowCriteria.add(new ArtifactTypeSearch(teamArtName, DepricatedOperator.EQUAL));
      FromArtifactsSearch teamWorkflowSearch = new FromArtifactsSearch(teamWorkflowCriteria, false);

      List<ISearchPrimitive> allCriteria = new LinkedList<ISearchPrimitive>();
      allCriteria.add(teamAndTaskSearch);
      allCriteria.add(teamWorkflowSearch);

      if (isCancelled()) return EMPTY_SET;
      Collection<Artifact> arts =
            ArtifactPersistenceManager.getInstance().getArtifacts(allCriteria, true,
                  BranchPersistenceManager.getAtsBranch());
      if (isCancelled()) return EMPTY_SET;
      return arts;
   }
}
