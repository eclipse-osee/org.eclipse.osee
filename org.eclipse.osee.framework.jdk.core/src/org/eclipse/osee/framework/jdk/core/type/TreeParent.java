/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

import java.util.ArrayList;

/**
 * @author Roberto E. Escobar
 */
public class TreeParent extends TreeObject {
   private ArrayList<TreeObject> children;

   public TreeParent(String name) {
      super(name);
      children = new ArrayList<TreeObject>();
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
      return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
   }

   public boolean hasChildren() {
      return children.size() > 0;
   }
}
