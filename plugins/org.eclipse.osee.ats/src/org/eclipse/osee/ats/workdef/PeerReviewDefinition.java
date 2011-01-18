/*
 * Created on Jan 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.util.ArrayList;
import java.util.List;

public class PeerReviewDefinition {

   public String name;
   public String reviewTitle;
   public String description = "";
   public String location = "";
   public String relatedToState;
   public ReviewBlockType blockingType;
   public StateEventType stateEventType;
   public List<String> assignees = new ArrayList<String>();

   public PeerReviewDefinition(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public ReviewBlockType getBlockingType() {
      return blockingType;
   }

   public void setBlockingType(ReviewBlockType blockingType) {
      this.blockingType = blockingType;
   }

   public StateEventType getStateEventType() {
      return stateEventType;
   }

   public void setStateEventType(StateEventType stateEventType) {
      this.stateEventType = stateEventType;
   }

   public List<String> getAssignees() {
      return assignees;
   }

   @Override
   public String toString() {
      return name;
   }

   public String getTitle() {
      return reviewTitle;
   }

   public void setReviewTitle(String reviewTitle) {
      this.reviewTitle = reviewTitle;
   }

   public String getRelatedToState() {
      return relatedToState;
   }

   public void setRelatedToState(String relatedToState) {
      this.relatedToState = relatedToState;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

}
