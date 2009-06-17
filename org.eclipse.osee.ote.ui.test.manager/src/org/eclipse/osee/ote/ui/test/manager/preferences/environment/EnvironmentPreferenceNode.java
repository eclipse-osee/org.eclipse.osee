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
package org.eclipse.osee.ote.ui.test.manager.preferences.environment;

import org.eclipse.osee.framework.jdk.core.type.TreeParent;

/**
 * @author Roberto E. Escobar
 */
public class EnvironmentPreferenceNode extends TreeParent {

   public boolean checked;
   public String value;

   public EnvironmentPreferenceNode(String name) {
      super(name);
      checked = false;
      value = "";
   }

   public String getEnvName() {
      return getName();
   }

   public String getValue() {
      return value;
   }

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean selected) {
      this.checked = selected;
   }

   public void setEnvName(String name) {
      setName(name);
   }

   public void setValue(String value) {
      this.value = value;
   }

}
