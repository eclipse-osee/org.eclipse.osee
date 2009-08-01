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
package org.eclipse.osee.framework.ui.data.model.editor.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditorInput implements IEditorInput {

   private DataTypeCache dataTypeCache;
   private IResource resource;

   public ODMEditorInput() {
      dataTypeCache = new DataTypeCache();
   }

   public ODMEditorInput(IResource resource) {
      this.resource = resource;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ODMEditorInput) {
         ODMEditorInput otherEdInput = (ODMEditorInput) obj;
      }
      return false;
   }

   public boolean exists() {
      return true;
   }

   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   public String getName() {
      return "No Data Types Provided";
   }

   public IPersistableElement getPersistable() {
      return null;
   }

   public String getToolTipText() {
      return getName();
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

   public DataTypeCache getDataTypeCache() {
      return dataTypeCache;
   }

   public IResource getResource() {
      return resource;
   }
}
