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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class TestManagerEditorInput implements IFileEditorInput, IPersistableElement {

   private static final String FACTORY_ID = TestManagerEditorInputFactory.class.getCanonicalName();
   private final IFile iFile;

   public TestManagerEditorInput(File file) {
      this(getIFile(file));
   }

   public TestManagerEditorInput(IFile iFile) {
      super();
      this.iFile = iFile;
   }
   
   private static IFile getIFile(File file){
      IFile ifile = AWorkspace.fileToIFile(file);
      if(ifile == null){
         IWorkspace workspace= ResourcesPlugin.getWorkspace();    
         IPath location= Path.fromOSString(file.getAbsolutePath()); 
         ifile= workspace.getRoot().getFileForLocation(location);
      }
      return ifile;
   }

   /*
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (iFile != null && o instanceof TestManagerEditorInput) {
         TestManagerEditorInput input = (TestManagerEditorInput) o;
         return iFile.equals(input.getFile());
      }
      return false;
   }

   /*
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   @Override
   public boolean exists() {
      return true;
   }
   
   @Override
   public <T> T getAdapter(Class<T> type) {
      return iFile.getAdapter(type);
   }

   @Override
   public String getFactoryId() {
      return FACTORY_ID;
   }

   @Override
   public IFile getFile() {
      return iFile;
   }

   /*
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   @Override
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
   @Override
   public String getName() {
      if(iFile == null){
         return "TestManager";
      }
      return iFile.getName();
   }

   /*
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   @Override
   public IPersistableElement getPersistable() {
      return this;
   }

   @Override
   public IStorage getStorage() {
      return new IStorage() {

         @Override
         public <T> T getAdapter(Class<T> type) {
            return iFile.getAdapter(type);
         }

         @Override
         public InputStream getContents() throws CoreException {
            return iFile.getContents();
         }

         @Override
         public IPath getFullPath() {
            return iFile.getFullPath();
         }

         @Override
         public String getName() {
            return iFile.getName();
         }

         @Override
         public boolean isReadOnly() {
            return false;
         }

      };
   }

   /*
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   @Override
   public String getToolTipText() {
      return iFile.getName();
   }

   /*
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return iFile.hashCode();
   }

   @Override
   public void saveState(IMemento memento) {
      if (iFile != null && iFile.getLocation().toFile().exists()) {
         memento.putString("path", iFile.getLocation().toFile().getAbsolutePath());
      }
   }
}
