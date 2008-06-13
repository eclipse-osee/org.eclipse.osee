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
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * @author Donald G. Dunne
 */
public class MyTaskSearchItem extends UserSearchItem {

   public MyTaskSearchItem(String name, LoadView loadView) {
      this(name, null, loadView);
   }

   public MyTaskSearchItem(String name, User user, LoadView loadView) {
      super(name, user);
      setLoadView(loadView);
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException, SQLException {
      Set<Artifact> assigned =
            RelationManager.getRelatedArtifacts(Arrays.asList(user), 1, CoreRelationEnumeration.Users_Artifact);

      List<Artifact> artifactsToReturn = new ArrayList<Artifact>(assigned.size());
      for (Artifact artifact : assigned) {
         if (artifact instanceof TaskArtifact) {
            artifactsToReturn.add(artifact);
         }
      }

      return artifactsToReturn;
   }
}
