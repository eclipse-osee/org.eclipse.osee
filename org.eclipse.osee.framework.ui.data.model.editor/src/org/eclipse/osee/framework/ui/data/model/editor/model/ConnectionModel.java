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
package org.eclipse.osee.framework.ui.data.model.editor.model;

import org.eclipse.draw2d.Graphics;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class ConnectionModel extends ModelElement {
   public static final Integer SOLID_CONNECTION = new Integer(Graphics.LINE_SOLID);
   public static final Integer DASHED_CONNECTION = new Integer(Graphics.LINE_DASH);
   public static final String LINESTYLE_PROP = "LineStyle";
   private static final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[1];
   private static final String SOLID_STR = "Solid";
   private static final String DASHED_STR = "Dashed";
   private static final long serialVersionUID = 1;

   private boolean isConnected;
   private int lineStyle = Graphics.LINE_SOLID;
   private BaseModel source;
   private BaseModel target;

   static {
      descriptors[0] =
            new ComboBoxPropertyDescriptor(LINESTYLE_PROP, LINESTYLE_PROP, new String[] {SOLID_STR, DASHED_STR});
   }

   public ConnectionModel(BaseModel source, BaseModel target) {
      reconnect(source, target);
   }

   public void disconnect() {
      if (isConnected) {
         source.removeConnection(this);
         target.removeConnection(this);
         isConnected = false;
      }
   }

   public int getLineStyle() {
      return lineStyle;
   }

   public IPropertyDescriptor[] getPropertyDescriptors() {
      return descriptors;
   }

   public Object getPropertyValue(Object id) {
      if (id.equals(LINESTYLE_PROP)) {
         if (getLineStyle() == Graphics.LINE_DASH)
         // Dashed is the second value in the combo dropdown
         return new Integer(1);
         // Solid is the first value in the combo dropdown
         return new Integer(0);
      }
      return super.getPropertyValue(id);
   }

   public BaseModel getSource() {
      return source;
   }

   public BaseModel getTarget() {
      return target;
   }

   public void reconnect() {
      if (!isConnected) {
         source.addConnection(this);
         target.addConnection(this);
         isConnected = true;
      }
   }

   public void reconnect(BaseModel newSource, BaseModel newTarget) {
      if (newSource == null || newTarget == null || newSource == newTarget) {
         throw new IllegalArgumentException();
      }
      disconnect();
      this.source = newSource;
      this.target = newTarget;
      reconnect();
   }

   public void setLineStyle(int lineStyle) {
      if (lineStyle != Graphics.LINE_DASH && lineStyle != Graphics.LINE_SOLID) {
         throw new IllegalArgumentException();
      }
      this.lineStyle = lineStyle;
      firePropertyChange(LINESTYLE_PROP, null, new Integer(this.lineStyle));
   }

   public void setPropertyValue(Object id, Object value) {
      if (id.equals(LINESTYLE_PROP))
         setLineStyle(new Integer(1).equals(value) ? Graphics.LINE_DASH : Graphics.LINE_SOLID);
      else
         super.setPropertyValue(id, value);
   }
}