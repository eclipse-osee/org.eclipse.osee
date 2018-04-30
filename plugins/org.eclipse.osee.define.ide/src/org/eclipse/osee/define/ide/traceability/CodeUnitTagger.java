/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability;

import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

/**
 * @author Ryan Rader
 */
public class CodeUnitTagger extends AbstractSourceTagger {

   private static final String ANNOTATION_STRING = "-- ObjectId(\"%s\")\n";

   private static final Pattern annotationPattern = Pattern.compile("-- ObjectId\\s*\\(\"(.*?)\"\\s*\\)");
   private static final Pattern importBlockPattern = Pattern.compile("(Proprietary header ends.*\\n)");

   private final Matcher annotationMatcher;
   private final Matcher importBlockMatcher;

   private static final CodeUnitTagger instance = new CodeUnitTagger();

   private CodeUnitTagger() {
      annotationMatcher = annotationPattern.matcher("");
      importBlockMatcher = importBlockPattern.matcher("");
   }

   public static CodeUnitTagger getInstance() {
      return instance;
   }

   @Override
   public String getSourceTag(CharBuffer buffer) {
      String toReturn = null;
      annotationMatcher.reset(buffer);
      if (annotationMatcher.find()) {
         toReturn = annotationMatcher.group(1);
      }
      return toReturn;
   }

   @Override
   public CharBuffer addSourceTag(CharBuffer buffer, String guid) {
      buffer = removeSourceTag(buffer);
      importBlockMatcher.reset(buffer);
      if (importBlockMatcher.find()) {
         int stop = importBlockMatcher.end();
         ChangeSet changeSet = new ChangeSet(buffer);
         changeSet.replace(stop, stop, String.format(ANNOTATION_STRING, guid));
         return CharBuffer.wrap(changeSet.applyChangesToSelf().toString().toCharArray());
      }
      return buffer;
   }

   @Override
   public CharBuffer removeSourceTag(CharBuffer buffer) {
      buffer = removeMatches(buffer, annotationMatcher);
      return buffer;
   }

}
