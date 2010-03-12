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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * @author Roberto E. Escobar
 */
public abstract class NodeModel extends Model {

   private final Point location;
   private final Dimension size;
   @SuppressWarnings("unchecked")
   private final List<ConnectionModel> sourceConnections;
   @SuppressWarnings("unchecked")
   private final List<ConnectionModel> targetConnections;

   @SuppressWarnings("unchecked")
   public NodeModel() {
      this.location = new Point(50, 50);
      this.size = new Dimension(100, 100);
      this.sourceConnections = new ArrayList<ConnectionModel>();
      this.targetConnections = new ArrayList<ConnectionModel>();

   }

   @SuppressWarnings("unchecked")
   void addConnection(ConnectionModel conn) {
      if (conn != null && conn.getSource() != conn.getTarget()) {
         if (conn.getSource() == this) {
            sourceConnections.add(conn);
            fireModelEvent();
         } else if (conn.getTarget() == this) {
            targetConnections.add(conn);
            fireModelEvent();
         }
      }
   }

   public Point getLocation() {
      return location.getCopy();
   }

   public Dimension getSize() {
      return size.getCopy();
   }

   public int getWidth() {
      return getSize().width;
   }

   public int getHeight() {
      return getSize().height;
   }

   @SuppressWarnings("unchecked")
   public List<ConnectionModel> getSourceConnections() {
      return new ArrayList<ConnectionModel>(sourceConnections);
   }

   @SuppressWarnings("unchecked")
   public List<ConnectionModel> getTargetConnections() {
      return new ArrayList<ConnectionModel>(targetConnections);
   }

   @SuppressWarnings("unchecked")
   void removeConnection(ConnectionModel conn) {
      if (conn != null) {
         if (conn.getSource() == this) {
            sourceConnections.remove(conn);
            fireModelEvent();
         } else if (conn.getTarget() == this) {
            targetConnections.remove(conn);
            fireModelEvent();
         }
      }
   }

   public void setLocation(Point newLocation) {
      if (newLocation != null && !newLocation.equals(getLocation())) {
         location.setLocation(newLocation);
         fireModelEvent(null);
      }
   }

   public void setSize(Dimension newSize) {
      if (newSize != null && !newSize.equals(getSize())) {
         size.setSize(newSize);
         fireModelEvent(null);
      }
   }

   public void setWidth(int newWidth) {
      Dimension dimension = getSize();
      dimension.width = newWidth;
      setSize(dimension);
   }

   @Override
   protected void fireModelEvent() {
      super.fireModelEvent();
      for (ConnectionModel conn : getTargetConnections()) {
         conn.fireModelEvent();
      }
      for (ConnectionModel conn : getSourceConnections()) {
         conn.fireModelEvent();
      }
   }

}