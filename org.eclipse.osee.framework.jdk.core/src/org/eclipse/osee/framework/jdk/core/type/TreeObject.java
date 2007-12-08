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

/**
 * @author Roberto E. Escobar
 */
public class TreeObject {
   private String name;
   private TreeParent parent;
   private boolean isCurrent;
   private boolean isChecked;

   public TreeObject(String name) {
      this.name = name;
   }

   public TreeObject() {
      this("");
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setParent(TreeParent parent) {
      this.parent = parent;
   }

   public TreeParent getParent() {
      return parent;
   }

   public String toString() {
      return getName();
   }

   public boolean isCurrent() {
      return isCurrent;
   }

   public void setCurrent(boolean isCurrent) {
      this.isCurrent = isCurrent;
   }

   public boolean isChecked() {
      return isChecked;
   }

   public void setChecked(boolean isChecked) {
      this.isChecked = isChecked;
   }

   public Object getAdapter(Class<?> adapter) {
      return null;
   }
}
