/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.world.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class MyWorldSearchItem extends UserSearchItem {

   public MyWorldSearchItem(String name, boolean currentUser) {
      this(name, null);
      setUseCurrentUser(currentUser);
   }

   public MyWorldSearchItem(String name, AtsUser user) {
      super(name, user, AtsImage.GLOBE);
   }

   public MyWorldSearchItem(MyWorldSearchItem myWorldSearchItem) {
      super(myWorldSearchItem);
   }

   @Override
   public Collection<Artifact> searchIt(AtsUser user) {
      Set<Artifact> assigned = AtsEditors.getAssigned(user);

      Set<Artifact> results = new HashSet<>(assigned.size());
      for (Artifact artifact : assigned) {
         if (artifact instanceof AbstractWorkflowArtifact) {
            if (artifact.isOfType(AtsArtifactTypes.Task)) {
               results.add(((TaskArtifact) artifact).getParentTeamWorkflow());
            } else {
               results.add(artifact);
            }
         }
      }
      return results;
   }

   @Override
   public WorldUISearchItem copy() {
      return new MyWorldSearchItem(this);
   }

}