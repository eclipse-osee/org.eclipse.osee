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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class MyCompletedSearchItem extends UserSearchItem {

   public MyCompletedSearchItem() {
      this("My Completed", null);
   }

   public MyCompletedSearchItem(String name) {
      super(name, null);
   }

   public MyCompletedSearchItem(String name, User user) {
      super(name, user);
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException, SQLException {
      if (isCancelled()) return EMPTY_SET;
      // SMA having user as portion of current state attribute (Team WorkFlow and Task)
      String valueToMatch =
            "%state=\"Completed\" type=\"StateEntered\" userId=\"" + getSearchUser().getUserId() + "\"%";
      return ArtifactQuery.getArtifactsFromAttribute(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), valueToMatch,
            AtsPlugin.getAtsBranch());
   }
}
