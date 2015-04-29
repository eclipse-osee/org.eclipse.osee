/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile.kanban;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxKbAvailableState {

   private String name;
   private int ordinal;
   private String stateType;
   private List<String> toStates = new ArrayList<String>();

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getOrdinal() {
      return ordinal;
   }

   public void setOrdinal(int ordinal) {
      this.ordinal = ordinal;
   }

   public String getStateType() {
      return stateType;
   }

   public void setStateType(String stateType) {
      this.stateType = stateType;
   }

   public List<String> getToStates() {
      return toStates;
   }

   public void setToStates(List<String> toStates) {
      this.toStates = toStates;
   }

}
