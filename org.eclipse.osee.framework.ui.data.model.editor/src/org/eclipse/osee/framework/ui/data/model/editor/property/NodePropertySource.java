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
package org.eclipse.osee.framework.ui.data.model.editor.property;

import java.util.List;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;
import org.eclipse.osee.framework.ui.plugin.views.property.IntegerPropertyDescriptor;
import org.eclipse.osee.framework.ui.plugin.views.property.ModelPropertySource;
import org.eclipse.osee.framework.ui.plugin.views.property.PropertyId;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class NodePropertySource extends ModelPropertySource {

   private final PropertyId idX;
   private final PropertyId idY;
   private final PropertyId idWidth;

   public NodePropertySource(String categoryName, Object model) {
      super(model);
      idX = new PropertyId(categoryName, "X");
      idY = new PropertyId(categoryName, "Y");
      idWidth = new PropertyId(categoryName, "Width");
   }

   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      list.add(new IntegerPropertyDescriptor(idX));
      list.add(new IntegerPropertyDescriptor(idY));
      IntegerPropertyDescriptor integerDescriptor = new IntegerPropertyDescriptor(idWidth);
      integerDescriptor.setValidator(new ICellEditorValidator() {
         public String isValid(Object value) {
            try {
               int val = Integer.parseInt((String) value);
               if (val == -1 || val > 0) return null;
            } catch (NumberFormatException nfe) {
            }
            return "The width has to be an integer greater than 0 (or -1 for default).";
         }
      });
      list.add(integerDescriptor);
   }

   protected NodeModel getNode() {
      return (NodeModel) getModel();
   }

   public Object getPropertyValue(Object id) {
      if (id == idX) return IntegerPropertyDescriptor.fromModel(getNode().getLocation().x);
      if (id == idY) return IntegerPropertyDescriptor.fromModel(getNode().getLocation().y);
      if (id == idWidth) return IntegerPropertyDescriptor.fromModel(getNode().getWidth());
      return null;
   }

   public boolean isPropertyResettable(Object id) {
      return id == idWidth;
   }

   public boolean isPropertySet(Object id) {
      if (id == idWidth) return getNode().getWidth() != -1;
      return false;
   }

   public void resetPropertyValue(Object id) {
      if (id == idWidth) getNode().setWidth(-1);
   }

   public void setPropertyValue(Object id, Object value) {
      if (id == idX) {
         Point newLoc = getNode().getLocation().getCopy();
         newLoc.x = IntegerPropertyDescriptor.toModel(value);
         getNode().setLocation(newLoc);
      } else if (id == idY) {
         Point newLoc = getNode().getLocation().getCopy();
         newLoc.y = IntegerPropertyDescriptor.toModel(value);
         getNode().setLocation(newLoc);
      } else if (id == idWidth) getNode().setWidth(IntegerPropertyDescriptor.toModel(value));
   }

}