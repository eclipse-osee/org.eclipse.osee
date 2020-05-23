/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.type;

import java.util.ArrayList;

/**
 * @author Roberto E. Escobar
 */
public class TreeParent extends TreeObject {
   private final ArrayList<TreeObject> children;

   public TreeParent(String name) {
      super(name);
      children = new ArrayList<>();
   }

   public TreeParent() {
      this("");
   }

   public void addChild(TreeObject child) {
      children.add(child);
      child.setParent(this);
   }

   public void removeChild(TreeObject child) {
      children.remove(child);
      child.setParent(null);
   }

   public TreeObject[] getChildren() {
      return children.toArray(new TreeObject[children.size()]);
   }

   public boolean hasChildren() {
      return children.size() > 0;
   }
}
