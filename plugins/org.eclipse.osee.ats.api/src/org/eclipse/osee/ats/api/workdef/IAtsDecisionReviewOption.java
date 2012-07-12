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
public interface IAtsDecisionReviewOption {

   public abstract String getName();

   public abstract void setName(String name);

   public abstract List<String> getUserIds();

   public abstract void setUserIds(List<String> userIds);

   public abstract boolean isFollowupRequired();

   public abstract void setFollowupRequired(boolean followupRequired);

   public abstract List<String> getUserNames();

   public abstract void setUserNames(List<String> userNames);

   @Override
   public abstract String toString();

}