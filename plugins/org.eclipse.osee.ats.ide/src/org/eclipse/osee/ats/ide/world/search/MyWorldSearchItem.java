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
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class MyWorldSearchItem extends UserSearchItem {

   private boolean useNewAttr;

   public MyWorldSearchItem(String name, boolean currentUser) {
      this(name, null);
      setUseCurrentUser(currentUser);
   }

   public MyWorldSearchItem(String name, boolean currentUser, boolean useNewAttr) {
      this(name, null);
      setUseCurrentUser(currentUser);
      this.useNewAttr = useNewAttr;
   }

   public MyWorldSearchItem(String name, AtsUser user) {
      this(name, user, false);
   }

   public MyWorldSearchItem(String name, AtsUser user, boolean useNewAttr) {
      super(name, user, AtsImage.GLOBE);
      this.useNewAttr = useNewAttr;
   }

   public MyWorldSearchItem(MyWorldSearchItem myWorldSearchItem) {
      super(myWorldSearchItem);
   }

   @Override
   public Collection<Artifact> searchIt(AtsUser user) {
      Collection<Artifact> assigned =
         Collections.castAll(AtsApiService.get().getQueryService().getAssigned(user));

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