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
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * @author Donald G. Dunne
 */
public class MyFavoritesSearchItem extends UserSearchItem {

   public MyFavoritesSearchItem(String name) {
      this(name, null);
   }

   public MyFavoritesSearchItem() {
      this("My Favorites", null);

   }

   public MyFavoritesSearchItem(String name, User user) {
      super(name, user);
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws Exception {
      return user.getArtifacts(RelationSide.FavoriteUser_Artifact);
   }

   @Override
   public void performUI(SearchType searchType) {
      if (user != null) return;
      super.performUI(searchType);
   }

}
