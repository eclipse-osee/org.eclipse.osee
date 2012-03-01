/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

import java.util.Collection;

public interface HasChildren {

   public Collection<IAtsObject> getChildren();

   public void addChild(IAtsObject child);

   public void removeChild(IAtsObject child);

   public void addChildren(Collection<IAtsObject> children);

   public void removeChildren(Collection<IAtsObject> children);

}
