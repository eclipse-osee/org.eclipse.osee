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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.swt.graphics.Image;

/**
 * Generic Artifact Label Provider showing Descriptive Name as text
 * 
 * @author Donald G. Dunne
 */
public class AttributeTypeLabelProvider implements ILabelProvider {

   public Image getImage(Object arg0) {
      return null;
   }

   public String getText(Object arg0) {
      if (arg0 instanceof AttributeType) {
         return ((AttributeType) arg0).getName();
      }
      return null;
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
