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
import org.eclipse.draw2d.Bendpoint;

/**
 * @author Roberto E. Escobar
 */
public class ConnectionModel extends Model {

   private boolean isConnected;
   private NodeModel source;
   private NodeModel target;
   private List<Bendpoint> bendPoints;

   public ConnectionModel() {
      this.bendPoints = new ArrayList<Bendpoint>();
   }

   public ConnectionModel(NodeModel source, NodeModel target) {
      this();
      reconnect(source, target);
   }

   public void disconnect() {
      if (isConnected) {
         source.removeConnection(this);
         target.removeConnection(this);
         isConnected = false;
      }
   }

   public NodeModel getSource() {
      return source;
   }

   public NodeModel getTarget() {
      return target;
   }

   public void setSource(NodeModel source) {
      this.source = source;
   }

   public void setTarget(NodeModel target) {
      this.target = target;
   }

   public void reconnect() {
      if (!isConnected) {
         source.addConnection(this);
         target.addConnection(this);
         isConnected = true;
      }
   }

   public void reconnect(NodeModel newSource, NodeModel newTarget) {
      if (newSource != null && newTarget != null && newSource != newTarget) {
         disconnect();
         this.source = newSource;
         this.target = newTarget;
         reconnect();
      }
   }

   public List<Bendpoint> getBendpoints() {
      return bendPoints;
   }
}