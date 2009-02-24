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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.plugin.views.property.ModelPropertySource;
import org.eclipse.osee.framework.ui.plugin.views.property.PropertyId;
import org.eclipse.osee.framework.ui.plugin.views.property.StringPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Roberto E. Escobar
 */
public class ConnectionPropertySource extends ModelPropertySource {

   private final PropertyId idBendpoints;

   public ConnectionPropertySource(String categoryName, Object model) {
      super(model);
      idBendpoints = new PropertyId(categoryName, "Bendpoints");
   }

   protected void addPropertyDescriptors(List<IPropertyDescriptor> list) {
      list.add(new StringPropertyDescriptor(idBendpoints));
   }

   public boolean isPropertyResettable(Object id) {
      return id == idBendpoints;
   }

   protected ConnectionModel<?> getConnectionModel() {
      return (ConnectionModel<?>) getModel();
   }

   public Object getPropertyValue(Object id) {
      if (id == idBendpoints) {
         StringBuffer buffer = new StringBuffer();
         for (Object object : getConnectionModel().getBendpoints()) {
            Bendpoint bendPoint = (Bendpoint) object;
            Point point = bendPoint.getLocation();
            buffer.append(point.x);
            buffer.append(',');
            buffer.append(point.y);
            buffer.append(' ');
         }
         return buffer.toString();
      }
      return null;
   }

   public boolean isPropertySet(Object id) {
      return id == idBendpoints && !getConnectionModel().getBendpoints().isEmpty();
   }

   public void resetPropertyValue(Object id) {
      if (id == idBendpoints) getConnectionModel().getBendpoints().clear();
   }

   public void setPropertyValue(Object id, Object value) {
      if (id == idBendpoints) {
         List<AbsoluteBendpoint> points = new ArrayList<AbsoluteBendpoint>();
         try {
            String[] result = ((String) value).split(" ");
            for (int i = 0; i < result.length; i++) {
               String[] coordinates = result[i].split(",");
               points.add(new AbsoluteBendpoint(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
            }
            getConnectionModel().getBendpoints().clear();
            getConnectionModel().getBendpoints().addAll(points);
         } catch (Exception e) {
         }
      }
   }

}