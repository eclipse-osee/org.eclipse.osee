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
 * @author John R. Misinco
 */
public class TestUnitTagger extends AbstractSourceTagger {

   private static final String ANNOTATION_STRING = "@ObjectId(\"%s\")\n";
   private static final String IMPORT_STRING = "\nimport org.eclipse.osee.framework.jdk.core.type.ObjectId;";

   private static final Pattern classPattern = Pattern.compile("public.*?\\s+class\\s+", Pattern.DOTALL);
   public static final Pattern ANNOTATION_PATTERN = Pattern.compile("@ObjectId\\s*\\(\"(.*?)\"\\s*\\)");
   private static final Pattern importPattern =
      Pattern.compile("import org\\.eclipse\\.osee\\.framework\\.jdk\\.core\\.type\\.ObjectId;");
   private static final Pattern importBlockPattern = Pattern.compile("(\\s*import\\s.*;)+");

   private final Matcher classMatcher;
   private final Matcher annotationMatcher;
   private final Matcher importMatcher;
   private final Matcher importBlockMatcher;

   private static final TestUnitTagger instance = new TestUnitTagger();

   private TestUnitTagger() {
      classMatcher = classPattern.matcher("");
      annotationMatcher = ANNOTATION_PATTERN.matcher("");
      importMatcher = importPattern.matcher("");
      importBlockMatcher = importBlockPattern.matcher("");
   }

   public static TestUnitTagger getInstance() {
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

   private CharBuffer addImportStatement(CharBuffer buffer) {
      importMatcher.reset(buffer);
      if (importMatcher.find()) {
         return buffer;
      } else {
         CharSequence copy = buffer.duplicate();
         importBlockMatcher.reset(copy);
         ChangeSet changeSet = new ChangeSet(copy);
         int position = 0;
         if (importBlockMatcher.find()) {
            position = importBlockMatcher.end();
            changeSet.insertBefore(position, IMPORT_STRING);
         }
         return CharBuffer.wrap(changeSet.applyChangesToSelf().toString().toCharArray());
      }
   }

   @Override
   public CharBuffer addSourceTag(CharBuffer buffer, String guid) {
      buffer = removeSourceTag(buffer);
      buffer = addImportStatement(buffer);
      classMatcher.reset(buffer);
      if (classMatcher.find()) {
         String classDeclaration = classMatcher.group();
         int start = classMatcher.start();
         int stop = classMatcher.end();
         ChangeSet changeSet = new ChangeSet(buffer);
         changeSet.replace(start, stop, String.format(ANNOTATION_STRING, guid) + classDeclaration);
         return CharBuffer.wrap(changeSet.applyChangesToSelf().toString().toCharArray());
      }
      return buffer;
   }

   @Override
   public CharBuffer removeSourceTag(CharBuffer buffer) {
      buffer = removeMatches(buffer, importMatcher);
      buffer = removeMatches(buffer, annotationMatcher);
      return buffer;
   }

}
