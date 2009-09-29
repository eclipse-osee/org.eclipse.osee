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
package org.eclipse.osee.coverage.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorInput implements IEditorInput {

   private final ICoverageEditorProvider coverageEditorProvider;

   public CoverageEditorInput(ICoverageEditorProvider coverageEditorProvider) {
      this.coverageEditorProvider = coverageEditorProvider;
   }

   @Override
   public boolean equals(Object obj) {
      return false;
   }

   public boolean exists() {
      return false;
   }

   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   public IPersistableElement getPersistable() {
      return null;
   }

   public String getToolTipText() {
      return "";
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   @Override
   public String getName() {
      return coverageEditorProvider.getName();
   }

   public ICoverageEditorProvider getCoverageEditorProvider() {
      return coverageEditorProvider;
   }

}
