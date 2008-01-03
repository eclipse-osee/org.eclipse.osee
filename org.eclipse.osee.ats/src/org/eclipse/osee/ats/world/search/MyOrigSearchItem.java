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
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.framework.skynet.core.User;
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
public class MyOrigSearchItem extends UserSearchItem {

   private final boolean onlyInWork;

   public MyOrigSearchItem(String name) {
      this(name, null, false);
   }

   public MyOrigSearchItem() {
      this("My Originator", null, false);
   }

   public MyOrigSearchItem(String name, User user, boolean onlyInWork) {
      super(name, user);
      this.onlyInWork = onlyInWork;
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws SQLException, IllegalArgumentException {

      // Find all Team Workflows artifact types
      List<ISearchPrimitive> teamWorkflowCriteria = new LinkedList<ISearchPrimitive>();
      for (String teamArtName : TeamWorkflowExtensions.getInstance().getAllTeamWorkflowArtifactNames())
         teamWorkflowCriteria.add(new ArtifactTypeSearch(teamArtName, Operator.EQUAL));
      FromArtifactsSearch teamWorkflowSearch = new FromArtifactsSearch(teamWorkflowCriteria, false);

      // SMA having user as portion of current state attribute (Team WorkFlow and Task)
      List<ISearchPrimitive> smaOrigCriteria = new LinkedList<ISearchPrimitive>();
      smaOrigCriteria.add(new AttributeValueSearch(ATSAttributes.LOG_ATTRIBUTE.getStoreName(),
            "%type=\"Originated\" userId=\"" + user.getUserId() + "\"%", Operator.LIKE));
      if (onlyInWork) {
         smaOrigCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Cancelled.name() + ";;;", Operator.NOT_EQUAL));
         smaOrigCriteria.add(new AttributeValueSearch(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
               DefaultTeamState.Completed.name() + ";;;", Operator.NOT_EQUAL));
      }
      smaOrigCriteria.add(teamWorkflowSearch);
      FromArtifactsSearch smaOrigSearch = new FromArtifactsSearch(smaOrigCriteria, true);

      if (isCancelled()) return EMPTY_SET;
      Collection<Artifact> arts =
            ArtifactPersistenceManager.getInstance().getArtifacts(
                  new InRelationSearch(smaOrigSearch, RelationSide.ActionToWorkflow_Action),
                  BranchPersistenceManager.getInstance().getAtsBranch());

      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

}
