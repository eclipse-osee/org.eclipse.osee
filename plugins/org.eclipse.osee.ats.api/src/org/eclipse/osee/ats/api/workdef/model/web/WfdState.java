/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workdef.model.web;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.jdk.core.type.NamedBase;

/**
 * @author Donald G. Dunne
 */
public class WfdState extends NamedBase {

   boolean startState;
   List<String> toStates = new ArrayList<>();
   StateType stateType;
   WfdWidgetComposite widgetComposite = new WfdWidgetComposite("");

   public WfdState() {
      // for jax-rs
   }

   public WfdState(String name) {
      super(name);
      widgetComposite.setName("[" + name + "] State Composite");
   }

   public boolean isStartState() {
      return startState;
   }

   public void setStartState(boolean startState) {
      this.startState = startState;
   }

   public List<String> getToStates() {
      return toStates;
   }

   public void setToStates(List<String> toStates) {
      this.toStates = toStates;
   }

   public StateType getStateType() {
      return stateType;
   }

   public void setStateType(StateType stateType) {
      this.stateType = stateType;
   }

   public WfdWidgetComposite getWidgetComposite() {
      return widgetComposite;
   }

   public void setWidgetComposite(WfdWidgetComposite widgetComposite) {
      this.widgetComposite = widgetComposite;
   }

}
