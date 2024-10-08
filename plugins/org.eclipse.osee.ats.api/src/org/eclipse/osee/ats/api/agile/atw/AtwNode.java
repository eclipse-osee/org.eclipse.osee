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

package org.eclipse.osee.ats.api.agile.atw;

import java.util.LinkedList;
import java.util.List;

/**
 * Format of this model must match what the angular-tree-widget is expecting. Test using OSEE Agile Web Program view if
 * changes are made.
 *
 * @author Donald G. Dunne
 */
public class AtwNode {

   String name;
   String image = null;
   String id;
   List<AtwNode> children = new LinkedList<>();
   boolean expanded = false;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getImage() {
      return image;
   }

   public void setImage(String image) {
      this.image = image;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public List<AtwNode> getChildren() {
      return children;
   }

   public void setChildren(List<AtwNode> children) {
      this.children = children;
   }

   @Override
   public String toString() {
      return "AtwNode [name=" + name + ", id=" + id + "]";
   }

   public boolean isExpanded() {
      return expanded;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

}
