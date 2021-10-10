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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class MyFavoritesSearchItem extends UserSearchItem {

   public MyFavoritesSearchItem(String name, AtsUser user) {
      super(name, user, AtsImage.FAVORITE);
   }

   public MyFavoritesSearchItem(MyFavoritesSearchItem myFavoritesSearchItem) {
      super(myFavoritesSearchItem, AtsImage.FAVORITE);
   }

   @Override
   protected Collection<Artifact> searchIt(AtsUser user) {
      return ArtifactQuery.getRelatedArtifactList(ArtifactToken.valueOf(user, COMMON), AtsRelationTypes.FavoriteUser,
         RelationSide.SIDE_B);
   }

   @Override
   public void performUI(SearchType searchType) {
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