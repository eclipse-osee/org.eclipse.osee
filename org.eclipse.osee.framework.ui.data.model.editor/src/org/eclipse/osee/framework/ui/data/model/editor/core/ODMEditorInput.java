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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   public boolean exists() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getName()
    */
   public String getName() {
      return "No Data Types Provided";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   public IPersistableElement getPersistable() {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   public String getToolTipText() {
      return getName();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      //      System.out.println(String.format("ODMEditorInput getAdapter for [%s]", adapter));
      return null;
   }

   public DataTypeCache getDataTypeCache() {
      return dataTypeCache;
   }

   public IResource getResource() {
      return resource;
   }
}
