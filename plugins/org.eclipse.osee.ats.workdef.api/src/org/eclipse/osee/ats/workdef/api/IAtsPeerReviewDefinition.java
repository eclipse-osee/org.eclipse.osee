/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

import java.util.List;

public interface IAtsPeerReviewDefinition {

   /**
    * Identification
    */
   public abstract String getName();

   public abstract void setName(String name);

   public abstract String getDescription();

   public abstract void setDescription(String description);

   /**
    * Created review options
    */
   public abstract ReviewBlockType getBlockingType();

   public abstract void setBlockingType(ReviewBlockType blockingType);

   public abstract StateEventType getStateEventType();

   public abstract void setStateEventType(StateEventType stateEventType);

   public abstract List<String> getAssignees();

   public abstract String getReviewTitle();

   public abstract void setReviewTitle(String reviewTitle);

   public abstract String getRelatedToState();

   public abstract void setRelatedToState(String relatedToState);

   public abstract String getLocation();

   public abstract void setLocation(String location);

   @Override
   public abstract String toString();

}