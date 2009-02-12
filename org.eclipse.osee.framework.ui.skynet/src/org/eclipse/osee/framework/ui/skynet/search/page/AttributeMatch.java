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
package org.eclipse.osee.framework.ui.skynet.search.page;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.Region;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
public class AttributeMatch extends Match implements IAdaptable {
   private Region fOriginalLocation;
   private AttributeLineElement fLineElement;

   public AttributeMatch(Artifact element, int offset, int length, AttributeLineElement lineEntry) {
      super(element, offset, length);
      fLineElement = lineEntry;
   }

   public void setOffset(int offset) {
      if (fOriginalLocation == null) {
         fOriginalLocation = new Region(getOffset(), getLength());
      }
      super.setOffset(offset);
   }

   public void setLength(int length) {
      if (fOriginalLocation == null) {
         fOriginalLocation = new Region(getOffset(), getLength());
      }
      super.setLength(length);
   }

   public int getOriginalOffset() {
      if (fOriginalLocation != null) {
         return fOriginalLocation.getOffset();
      }
      return getOffset();
   }

   public int getOriginalLength() {
      if (fOriginalLocation != null) {
         return fOriginalLocation.getLength();
      }
      return getLength();
   }

   public Artifact getArtifact() {
      return (Artifact) getElement();
   }

   public AttributeLineElement getLineElement() {
      return fLineElement;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter) {
      if (Artifact.class.equals(adapter)) {
         return this.getArtifact();
      }
      return null;
   }
}
