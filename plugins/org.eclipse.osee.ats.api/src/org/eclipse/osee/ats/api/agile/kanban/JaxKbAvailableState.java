/*********************************************************************
 * Copyright (c) 2017 Boeing
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
   private List<String> toStates = new ArrayList<>();

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
