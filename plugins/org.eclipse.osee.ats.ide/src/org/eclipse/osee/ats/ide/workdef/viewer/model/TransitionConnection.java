/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
�* All rights reserved. This program and the accompanying materials
�* are made available under the terms of the Eclipse Public License v1.0
�* which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.ide.workdef.viewer.model;

import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * A connection between two distinct shapes.
 * 
 * @author Donald G. Dunne
 */
public class TransitionConnection extends Connection {

   /** Property ID to use when the line style of this connection is modified. */
   public static final String TYPE_PROP = "Type";
   private static final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[1];

   static {
      descriptors[0] = new TextPropertyDescriptor(TYPE_PROP, TYPE_PROP);
   }

   public TransitionConnection(Shape source, Shape target) {
      super(source, target);
   }

   @Override
   public Color getForegroundColor() {
      return Displays.getSystemColor(SWT.COLOR_BLACK);
   }

   @Override
   public String toString() {
      return "Transition: " + getPropertyValue(TYPE_PROP);
   }

   @Override
   public Result validForSave() {
      System.err.println("Add Connection validations.");
      return Result.TrueResult;
   }

   /**
    * Returns the descriptor for the lineStyle property
    * 
    * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
    */
   @Override
   public IPropertyDescriptor[] getPropertyDescriptors() {
      return descriptors;
   }

   /**
    * Returns the lineStyle as String for the Property Sheet
    * 
    * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
    */
   @Override
   public Object getPropertyValue(Object id) {
      if (id.equals(TYPE_PROP)) {
         return getLabel();
      }
      return super.getPropertyValue(id);
   }

   @Override
   public String getLabel() {
      return "Transition";
   }

   @Override
   public int getLineWidth() {
      return 2;
   }

   @Override
   public Result doSave(SkynetTransaction transaction) {
      return Result.TrueResult;
   }

}