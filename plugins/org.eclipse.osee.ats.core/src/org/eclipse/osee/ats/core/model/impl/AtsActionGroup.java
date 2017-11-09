/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

   List<IAtsWorkItem> actions = new ArrayList<>();

   public AtsActionGroup(String guid, String name, long id) {
      super(guid, id);
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
