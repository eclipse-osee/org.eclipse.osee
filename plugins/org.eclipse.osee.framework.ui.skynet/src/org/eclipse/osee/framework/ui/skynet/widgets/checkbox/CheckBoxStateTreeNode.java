/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.checkbox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class CheckBoxStateTreeNode {
   private final String name;
   private final CheckBoxStateTreeNode parent;
   private List<CheckBoxStateTreeNode> children = new ArrayList<>();
   private boolean checked;
   private boolean enabled = true;
   private Object data;

   public CheckBoxStateTreeNode(String name, Object data, CheckBoxStateTreeNode parent) {
      this.name = name;
      this.data = data;
      this.parent = parent;
      if (parent != null) {
         parent.addChild(this);
      }
   }

   public void setCheckAll(boolean checked) {
      this.checked = checked;
      for (CheckBoxStateTreeNode child : children) {
         child.setChecked(checked);
      }
   }

   public void addChild(CheckBoxStateTreeNode child) {
      children.add(child);
   }

   public List<CheckBoxStateTreeNode> getChildren() {
      return children;
   }

   public void setChildren(List<CheckBoxStateTreeNode> children) {
      this.children = children;
   }

   public String getName() {
      return name;
   }

   public CheckBoxStateTreeNode getParent() {
      return parent;
   }

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   @Override
   public String toString() {
      return name;
   }

   public Object getData() {
      return data;
   }

   public void setData(Object data) {
      this.data = data;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
