/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
   private final AttributeLineElement fLineElement;

   public AttributeMatch(Artifact element, int offset, int length, AttributeLineElement lineEntry) {
      super(element, offset, length);
      fLineElement = lineEntry;
   }

   @Override
   public void setOffset(int offset) {
      if (fOriginalLocation == null) {
         fOriginalLocation = new Region(getOffset(), getLength());
      }
      super.setOffset(offset);
   }

   @Override
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

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getAdapter(Class<T> type) {
      if (type != null && type.isAssignableFrom(Artifact.class)) {
         return (T) getArtifact();
      }

      Object obj = null;
      T object = (T) obj;
      return object;
   }
}
