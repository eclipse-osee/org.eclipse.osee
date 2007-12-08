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
package org.eclipse.osee.framework.skynet.core.tagging;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Ryan D. Brooks
 */
public abstract class Tagger {
   private Set<String> tags;

   public Tagger() {
      tags = new HashSet<String>(1000, .75f);
   }

   public boolean isValidFor(Artifact artifact) {
      return true;
   }

   public String toString() {
      return getClass().getName();
   }

   public Set<String> getTags(Artifact artifact) throws SQLException {
      tags.clear();

      Collection<String> rawTokens = getTextStrings(artifact);
      for (String rawToken : rawTokens) {
         if (rawToken != null && !rawToken.equals("")) {
            tags.addAll(tokenizeAndSplit(rawToken));
         }
      }
      return tags;
   }

   /**
    * Phase 1.5 - tokenize (discard separators) based on 0-32, 34, 127 Phase 2 - split (keep separators) based on 33 -
    * 47, 58 - 64, 91 - 94, 96, 123 - 126 Provides simple Decimal and Hex ASCII Chart of http://www.asciichart.com/
    */
   public static final List<String> tokenizeAndSplit(String text) {
      List<String> tags = new LinkedList<String>();
      int tagStartIndex = 0;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c <= 47 || (c >= 58 && c <= 64) || (c >= 91 && c <= 94) || c == 96 || (c >= 123 && c <= 127)) { // if is a separator
            if (i - tagStartIndex > 0) { //if tag prior to this char has a length greater than zero
               //limit tags to 50 chars
               tags.add(Strings.truncate(text.substring(tagStartIndex, i), 50));
            }
            if (c == 33 || (c > 34 && c < 127)) { // i.e. c is a value is in splitting for phase two (note: the parent if statement already did some filtering)
               tags.add(String.valueOf(c));
            }
            tagStartIndex = i + 1;
         }
      }

      if (tagStartIndex < text.length()) {
         tags.add(Strings.truncate(text.substring(tagStartIndex, text.length()), 50));
      }
      return tags;
   }

   /**
    * Implementations should return relevant text from the given artifact. Often this means the tagger will filter out
    * all file format data (non-content) and possibly also filter content considered unimportant. Typically the content
    * is directly stored in the attributes of this artifact, but the tagger could use higher level logic to return text
    * that is not directly extracted from the artifact.
    */
   public abstract Collection<String> getTextStrings(Artifact artifact) throws SQLException;
}
