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
public class ConnectionModel<T extends NodeModel> extends Model {

   private boolean isConnected;
   private T source;
   private T target;
   private List<Bendpoint> bendPoints;

   public ConnectionModel() {
      this.bendPoints = new ArrayList<Bendpoint>();
   }

   public ConnectionModel(T source, T target) {
      this();
      reconnect(source, target);
   }

   public void disconnect() {
      if (isConnected) {
         source.removeConnection(this);
         target.removeConnection(this);
         if (this.source != null) {
            for (IModelListener listener : getListeners()) {
               this.source.removeListener(listener);
            }
         }
         if (this.target != null) {
            for (IModelListener listener : getListeners()) {
               this.target.removeListener(listener);
            }
         }
         isConnected = false;
         //         fireModelEvent();
      }
   }

   public T getSource() {
      return source;
   }

   public T getTarget() {
      return target;
   }

   public void setSource(T source) {
      if (this.source != source) {
         if (this.source != null) {
            for (IModelListener listener : getListeners()) {
               this.source.removeListener(listener);
            }
         }
         this.source = source;
         //         fireModelEvent();
      }
   }

   public void setTarget(T target) {
      if (this.target != target) {
         if (this.target != null) {
            for (IModelListener listener : getListeners()) {
               this.target.removeListener(listener);
            }
         }
         this.target = target;
         //         fireModelEvent();
      }
   }

   public void reconnect() {
      if (!isConnected) {
         source.addConnection(this);
         target.addConnection(this);
         isConnected = true;
         //         fireModelEvent();
      }
   }

   public void reconnect(T newSource, T newTarget) {
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.model.Model#addListener(org.eclipse.osee.framework.ui.data.model.editor.model.IModelListener)
    */
   @Override
   public void addListener(IModelListener listener) {
      if (getSource() != null) {
         getSource().addListener(listener);
      }
      if (getTarget() != null) {
         getTarget().addListener(listener);
      }
      super.addListener(listener);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.model.Model#removeListener(org.eclipse.osee.framework.ui.data.model.editor.model.IModelListener)
    */
   @Override
   public void removeListener(IModelListener listener) {
      if (getSource() != null) {
         getSource().removeListener(listener);
      }
      if (getTarget() != null) {
         getTarget().removeListener(listener);
      }
      super.removeListener(listener);
   }

}