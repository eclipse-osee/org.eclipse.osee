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
   private final List<ConnectionModel> sourceConnections;
   private final List<ConnectionModel> targetConnections;

   public NodeModel() {
      this.location = new Point(0, 0);
      this.size = new Dimension(50, 50);
      this.sourceConnections = new ArrayList<ConnectionModel>();
      this.targetConnections = new ArrayList<ConnectionModel>();

   }

   void addConnection(ConnectionModel conn) {
      if (conn != null && conn.getSource() != conn.getTarget()) {
         if (conn.getSource() == this) {
            sourceConnections.add(conn);
         } else if (conn.getTarget() == this) {
            targetConnections.add(conn);
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

   public List<ConnectionModel> getOutgoingConnections() {
      return new ArrayList<ConnectionModel>(sourceConnections);
   }

   public List<ConnectionModel> getIncomingConnections() {
      return new ArrayList<ConnectionModel>(targetConnections);
   }

   void removeConnection(ConnectionModel conn) {
      if (conn != null) {
         if (conn.getSource() == this) {
            sourceConnections.remove(conn);
         } else if (conn.getTarget() == this) {
            targetConnections.remove(conn);
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
}