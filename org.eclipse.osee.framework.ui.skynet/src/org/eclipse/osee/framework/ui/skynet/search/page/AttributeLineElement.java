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

import java.util.ArrayList;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
public class AttributeLineElement implements IAdaptable {

   private final Attribute<?> attribute;
   private final Artifact parent;
   private final int lineNumber;
   private final int lineStartOffset;
   private final String lineContents;

   public AttributeLineElement(Artifact parent, Attribute<?> attribute, int lineNumber, int lineStartOffset, String contents) {
      this.parent = parent;
      this.attribute = attribute;
      this.lineContents = contents;
      this.lineNumber = lineNumber;
      this.lineStartOffset = lineStartOffset;
   }

   public Artifact getParent() {
      return parent;
   }

   public int getLine() {
      return lineNumber;
   }

   public String getContents() {
      return lineContents;
   }

   public int getOffset() {
      return lineStartOffset;
   }

   public boolean contains(int offset) {
      return lineStartOffset <= offset && offset < lineStartOffset + lineContents.length();
   }

   public int getLength() {
      return lineContents.length();
   }

   public AttributeMatch[] getMatches(AbstractArtifactSearchResult result) {
      ArrayList<AttributeMatch> res = new ArrayList<AttributeMatch>();
      Match[] matches = result.getMatches(parent);
      for (int i = 0; i < matches.length; i++) {
         AttributeMatch curr = (AttributeMatch) matches[i];
         if (curr.getLineElement() == this) {
            res.add(curr);
         }
      }
      return res.toArray(new AttributeMatch[res.size()]);
   }

   public int getNumberOfMatches(AbstractArtifactSearchResult result) {
      int count = 0;
      Match[] matches = result.getMatches(parent);
      for (int i = 0; i < matches.length; i++) {
         AttributeMatch curr = (AttributeMatch) matches[i];
         if (curr.getLineElement() == this) {
            count++;
         }
      }
      return count;
   }

   public Attribute<?> getAttribute() {
      return attribute;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == Artifact.class) {
         return getParent();
      } else if (adapter == Attribute.class) {
         return getAttribute();
      }
      return null;
   }

}
