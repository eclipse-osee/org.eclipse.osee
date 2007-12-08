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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public enum ChangeType {

   None, Support, Problem, Improvement;

   public static String[] getChangeTypes() {
      ArrayList<String> types = new ArrayList<String>();
      for (ChangeType type : values())
         if (type != None) types.add(type.name());
      return types.toArray(new String[types.size()]);
   }

   public static ChangeType getChangeType(String name) {
      for (ChangeType type : values()) {
         if (type.name().equals(name)) return type;
      }
      return None;
   }

   public Image getImage() {
      if (this == ChangeType.Problem)
         return SkynetGuiPlugin.getInstance().getImage("greenBug.gif");
      else if (this == ChangeType.Improvement)
         return SkynetGuiPlugin.getInstance().getImage("greenPlus.gif");
      else if (this == ChangeType.Support) return SkynetGuiPlugin.getInstance().getImage("users2.gif");
      return null;
   }

   public static class ChangeTypeLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return ((ChangeType) arg0).getImage();
      }

      public String getText(Object arg0) {
         return ((ChangeType) arg0).name();
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }

   }

}
