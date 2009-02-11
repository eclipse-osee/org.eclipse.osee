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
import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactXmlQueryResultParser.MatchLocation;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
public class AttributeLineElement implements IAdaptable {

   private final Attribute<?> attribute;
   private final Artifact parent;
   private final MatchLocation location;
   private String contents;

   public AttributeLineElement(Artifact parent, Attribute<?> attribute, MatchLocation location) {
      this.parent = parent;
      this.attribute = attribute;
      this.location = location;
      this.contents = null;
   }

   public Artifact getParent() {
      return parent;
   }

   public int getStartAt() {
      int index = location.getStartPosition() - 1;
      if (index >= 0) {
         return index;
      }
      return location.getStartPosition();
   }

   public int getStopAt() {
      int index = location.getEndPosition() - 1;
      if (index >= getStartAt()) {
         return index;
      }
      return location.getEndPosition();
   }

   public String getContents() {
      if (contents == null) {
         contents = getContentFromAttribute();
         if (Strings.isValid(contents)) {
            contents = getContents(contents, getStartAt(), getStopAt());
         }
      }
      return contents;
   }

   private String getContents(String content, int start, int end) {
      StringBuffer buf = new StringBuffer();
      for (int i = start; i < end; i++) {
         char ch = content.charAt(i);
         if (Character.isWhitespace(ch) || Character.isISOControl(ch)) {
            buf.append(' ');
         } else {
            buf.append(ch);
         }
      }
      return buf.toString();
   }

   private String getContentFromAttribute() {
      try {
         Object value = attribute.getValue();
         if (value instanceof String) {
            return (String) value;
         } else {
            return attribute.getDisplayableString();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return "";
   }

   public int getOffset() {
      return getStartAt();
   }

   public boolean contains(int offset) {
      return getStartAt() <= offset && offset < getStopAt();
   }

   public int getLength() {
      return getStopAt() - getStartAt();
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

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == Artifact.class) {
         return getParent();
      } else if (adapter == Attribute.class) {
         return attribute;
      }
      return null;
   }

}
