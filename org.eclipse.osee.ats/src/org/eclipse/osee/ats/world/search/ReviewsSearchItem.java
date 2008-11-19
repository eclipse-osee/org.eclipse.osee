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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.DepricatedOperator;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;

/**
 * Returns all reviews that user had a role in
 * 
 * @author Donald G. Dunne
 */
public class ReviewsSearchItem extends UserSearchItem {

   public ReviewsSearchItem(String name) {
      this(name, null);
   }

   public ReviewsSearchItem(String name, User user) {
      super(name, user);
   }

   public ReviewsSearchItem(ReviewsSearchItem reviewsSearchItem) {
      super(reviewsSearchItem);
   }

   @Override
   protected Collection<Artifact> searchIt(User user) throws OseeCoreException {

      // SMA having user as portion of current state attribute (Team WorkFlow and Task)
      List<ISearchPrimitive> currentStateCriteria = new LinkedList<ISearchPrimitive>();
      currentStateCriteria.add(new AttributeValueSearch(ATSAttributes.ROLE_ATTRIBUTE.getStoreName(),
            "<" + user.getUserId() + ">", DepricatedOperator.CONTAINS));
      FromArtifactsSearch currentStateSearch = new FromArtifactsSearch(currentStateCriteria, false);

      // Find all Team Workflows artifact types
      List<ISearchPrimitive> reviewTypeCriteria = new LinkedList<ISearchPrimitive>();
      for (String reviewArtName : ReviewManager.getAllReviewArtifactTypeNames())
         reviewTypeCriteria.add(new ArtifactTypeSearch(reviewArtName, DepricatedOperator.EQUAL));
      FromArtifactsSearch reviewArtSearch = new FromArtifactsSearch(reviewTypeCriteria, false);

      List<ISearchPrimitive> allCriteria = new LinkedList<ISearchPrimitive>();
      allCriteria.add(currentStateSearch);
      allCriteria.add(reviewArtSearch);

      if (isCancelled()) return EMPTY_SET;
      Collection<Artifact> arts = ArtifactPersistenceManager.getArtifacts(allCriteria, true, AtsPlugin.getAtsBranch());
      if (isCancelled()) return EMPTY_SET;
      return arts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new ReviewsSearchItem(this);
   }

}
