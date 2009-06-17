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
package org.eclipse.osee.ote.ui.test.manager.core;

import java.io.File;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class TestManagerEditorInput implements IFileEditorInput, IPersistableElement {

   private static final String FACTORY_ID = TestManagerEditorInputFactory.class.getCanonicalName();
   private IFile iFile;

   public TestManagerEditorInput(File file) {
      this(AWorkspace.fileToIFile(file));
   }

   public TestManagerEditorInput(IFile iFile) {
      super();
      this.iFile = iFile;
   }

   /*
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object o) {
      if (o == this)
         return true;

      if (iFile != null && o instanceof TestManagerEditorInput) {
         TestManagerEditorInput input = (TestManagerEditorInput) o;
         return iFile.equals(input.getFile());
      }
      return false;
   }

   /*
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   public boolean exists() {
      return true;
   }

   /*
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return iFile.getAdapter(adapter);
   }

   public String getFactoryId() {
      return FACTORY_ID;
   }

   public IFile getFile() {
      return iFile;
   }

   /*
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   // /*
   // * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
   // */
   // public IPath getPath(Object element) {
   // if (element instanceof NonExistingFileEditorInput) {
   // NonExistingFileEditorInput input= (NonExistingFileEditorInput) element;
   // return Path.fromOSString(input.fFile.getAbsolutePath());
   // }
   // return null;
   // }

   /*
    * @see org.eclipse.ui.IEditorInput#getName()
    */
   public String getName() {
      return iFile.getName();
   }

   /*
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   public IPersistableElement getPersistable() {
      return this;
   }

   public IStorage getStorage() throws CoreException {
      return new IStorage() {

         @SuppressWarnings("unchecked")
         public Object getAdapter(Class adapter) {
            return iFile.getAdapter(adapter);
         }

         public InputStream getContents() throws CoreException {
            return iFile.getContents();
         }

         public IPath getFullPath() {
            return iFile.getFullPath();
         }

         public String getName() {
            return iFile.getName();
         }

         public boolean isReadOnly() {
            return false;
         }

      };
   }

   /*
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   public String getToolTipText() {
      return iFile.getName();
   }

   /*
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      return iFile.hashCode();
   }

   public void saveState(IMemento memento) {
      if (iFile != null && iFile.getLocation().toFile().exists()) {
         memento.putString("path", iFile.getLocation().toFile().getAbsolutePath());
      }
   }
}
