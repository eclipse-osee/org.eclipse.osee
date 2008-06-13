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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

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
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException, SQLException {

      Set<Artifact> assigned =
            RelationManager.getRelatedArtifacts(Arrays.asList(user), 1, CoreRelationEnumeration.Users_Artifact);

      Set<Artifact> artifacts =
            RelationManager.getRelatedArtifacts(assigned, 3, AtsRelation.SmaToTask_Sma,
                  AtsRelation.TeamWorkflowToReview_Team);

      // Because user can be assigned directly to workflow or through being assigned to task, add in
      // all the original artifacts to search through also.
      artifacts.addAll(assigned);

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         if (artifact instanceof TeamWorkFlowArtifact) {
            artifactsToReturn.add(artifact);
         }
      }

      return artifactsToReturn;
   }
}
