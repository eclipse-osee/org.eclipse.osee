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
package org.eclipse.osee.ats.api.agile.program;

import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * Format of this model must match what the angular-tree-widget is expecting. Test using OSEE Agile Web Program view if
 * changes are made.
 *
 * @author Donald G. Dunne
 */
public class UiGridProgItem extends JaxAtsObject {

   String image = null;
   String type;
   int tLevel;
   boolean expanded = false;
   String agilePoints;
   String assigneesOrImplementers;

   public String getImage() {
      return image;
   }

   public void setImage(String image) {
      this.image = image;
   }

   @Override
   public String toString() {
      return "ProgItem [name=" + getName() + ", id=" + id + "]";
   }

   public boolean isExpanded() {
      return expanded;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public int getTLevel() {
      return tLevel;
   }

   public void setTLevel(int treeLevel) {
      this.tLevel = treeLevel;
   }

   public String getAgilePoints() {
      return agilePoints;
   }

   public void setAgilePoints(String agilePoints) {
      this.agilePoints = agilePoints;
   }

   public String getAssigneesOrImplementers() {
      return assigneesOrImplementers;
   }

   public void setAssigneesOrImplementers(String assigneesOrImplementers) {
      this.assigneesOrImplementers = assigneesOrImplementers;
   }

}
