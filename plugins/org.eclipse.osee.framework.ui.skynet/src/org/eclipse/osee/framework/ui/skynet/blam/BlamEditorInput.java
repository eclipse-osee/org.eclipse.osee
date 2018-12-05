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
package org.eclipse.osee.framework.ui.skynet.blam;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class BlamEditorInput implements IEditorInput, IPersistableElement, Adaptable {

   private final AbstractBlam blamOperation;

   public BlamEditorInput(AbstractBlam blamOperation) {
      this.blamOperation = blamOperation;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BlamEditorInput) {
         return ((BlamEditorInput) obj).getBlamOperation().equals(getBlamOperation());
      }
      return false;
   }

   @Override
   public String getName() {
      return blamOperation.getTitle();
   }

   public Image getImage() {
      return blamOperation.getImage();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return blamOperation.getImageDescriptor();
   }

   public AbstractBlam getBlamOperation() {
      return blamOperation;
   }

   @Override
   public boolean exists() {
      return false;
   }

   @Override
   public IPersistableElement getPersistable() {
      return this;
   }

   @Override
   public String getToolTipText() {
      return "";
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public void saveState(IMemento memento) {
      BlamEditorInputFactory.saveState(memento, this);
   }

   @Override
   public String getFactoryId() {
      return BlamEditorInputFactory.ID;
   }

}
