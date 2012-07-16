/*
 * Created on Feb 28, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.model.IActionGroup;

/**
 * @author Donald G. Dunne
 */
public class AtsActionGroup extends AtsObject implements IActionGroup {

   List<IAtsWorkItem> actions = new ArrayList<IAtsWorkItem>();

   public AtsActionGroup(String name) {
      super(name);
   }

   @Override
   public Collection<IAtsWorkItem> getActions() {
      return actions;
   }

   @Override
   public IAtsWorkItem getFirstAction() {
      if (actions.size() > 0) {
         return actions.iterator().next();
      }
      return null;
   }

   public void addAction(IAtsWorkItem action) {
      this.actions.add(action);
   }

   public void setActions(List<? extends IAtsWorkItem> actions) {
      this.actions.clear();
      this.actions.addAll(actions);
   }

}
