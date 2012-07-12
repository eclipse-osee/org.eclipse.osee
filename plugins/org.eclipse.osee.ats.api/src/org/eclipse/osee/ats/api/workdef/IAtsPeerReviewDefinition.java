/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsPeerReviewDefinition {

   /**
    * Identification
    */
   public abstract String getName();

   public abstract String getDescription();

   /**
    * Created review options
    */
   public abstract ReviewBlockType getBlockingType();

   public abstract StateEventType getStateEventType();

   public abstract List<String> getAssignees();

   public abstract String getReviewTitle();

   public abstract String getRelatedToState();

   public abstract String getLocation();

   @Override
   public abstract String toString();

}