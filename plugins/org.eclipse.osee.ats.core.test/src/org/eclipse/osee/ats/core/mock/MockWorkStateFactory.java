/*
 * Created on Mar 8, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.mock;

import java.util.List;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.WorkState;
import org.eclipse.osee.ats.core.model.WorkStateFactory;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;

public class MockWorkStateFactory implements WorkStateFactory {

   @Override
   public WorkState createStateData(String name, List<? extends IAtsUser> assignees) {
      return new WorkStateImpl(name, assignees);
   }

   @Override
   public WorkState createStateData(String name) {
      return new WorkStateImpl(name);
   }

   @Override
   public WorkState createStateData(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) {
      return new WorkStateImpl(name, assignees, hoursSpent, percentComplete);
   }

}
