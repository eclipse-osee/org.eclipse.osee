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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public abstract class ModelElement implements IPropertySource, Serializable {
   private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];
   private static final long serialVersionUID = 1;
   private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

   public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
      if (l == null) {
         throw new IllegalArgumentException();
      }
      pcsDelegate.addPropertyChangeListener(l);
   }

   protected void firePropertyChange(String property, Object oldValue, Object newValue) {
      if (pcsDelegate.hasListeners(property)) {
         pcsDelegate.firePropertyChange(property, oldValue, newValue);
      }
   }

   public Object getEditableValue() {
      return this;
   }

   public IPropertyDescriptor[] getPropertyDescriptors() {
      return EMPTY_ARRAY;
   }

   public Object getPropertyValue(Object id) {
      return null;
   }

   public boolean isPropertySet(Object id) {
      return false;
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      pcsDelegate = new PropertyChangeSupport(this);
   }

   public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
      if (l != null) {
         pcsDelegate.removePropertyChangeListener(l);
      }
   }

   public void resetPropertyValue(Object id) {
      // do nothing
   }

   public void setPropertyValue(Object id, Object value) {
      // do nothing
   }
}
