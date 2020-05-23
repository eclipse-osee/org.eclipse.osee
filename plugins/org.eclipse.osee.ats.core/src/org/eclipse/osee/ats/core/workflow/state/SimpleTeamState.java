/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.core.workflow.state;

import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;

/**
 * @author Donald G. Dunne
 */
public class SimpleTeamState extends StateTypeAdapter {
   private final String name;

   public SimpleTeamState(String name, StateType StateType) {
      super(SimpleTeamState.class, name, StateType);
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

}
