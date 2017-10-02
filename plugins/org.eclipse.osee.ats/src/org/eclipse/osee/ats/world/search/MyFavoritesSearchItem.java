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
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class MyFavoritesSearchItem extends UserSearchItem {

   public MyFavoritesSearchItem(String name, IAtsUser user) {
      super(name, user, AtsImage.FAVORITE);
   }

   public MyFavoritesSearchItem(MyFavoritesSearchItem myFavoritesSearchItem) {
      super(myFavoritesSearchItem, AtsImage.FAVORITE);
   }

   @Override
   protected Collection<Artifact> searchIt(IAtsUser user)  {
      return AtsClientService.get().getUserServiceClient().getOseeUser(user).getRelatedArtifacts(
         AtsRelationTypes.FavoriteUser_Artifact);
   }

   @Override
   public void performUI(SearchType searchType)  {
      if (user != null) {
         return;
      }
      super.performUI(searchType);
   }

   @Override
   public WorldUISearchItem copy() {
      return new MyFavoritesSearchItem(this);
   }

}
