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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class MyWorldSearchItem extends UserSearchItem {

   public MyWorldSearchItem(String name) {
      this(name, null);
   }

   public MyWorldSearchItem() {
      super("My World", null);
   }

   public MyWorldSearchItem(String name, User user) {
      super(name, user);
   }

   public MyWorldSearchItem(MyWorldSearchItem myWorldSearchItem) {
      super(myWorldSearchItem);
   }

   @Override
   public Collection<Artifact> searchIt(User user) throws OseeCoreException {
      Set<Artifact> assigned =
            RelationManager.getRelatedArtifacts(Arrays.asList(user), 1, CoreRelationEnumeration.Users_Artifact);

      Set<Artifact> artifacts =
            RelationManager.getRelatedArtifacts(assigned, 4, AtsRelation.SmaToTask_Sma,
                  AtsRelation.TeamWorkflowToReview_Team, AtsRelation.ActionToWorkflow_Action);

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         if (artifact instanceof ActionArtifact) {
            artifactsToReturn.add(artifact);
         }
      }
      return artifactsToReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new MyWorldSearchItem(this);
   }

}