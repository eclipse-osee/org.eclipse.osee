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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.regex.Matcher;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author John R. Misinco
 */
public abstract class AbstractSourceTagger {

   public abstract String getSourceTag(CharBuffer buffer);

   public abstract CharBuffer removeSourceTag(CharBuffer buffer);

   public abstract CharBuffer addSourceTag(CharBuffer buffer, String tag);

   public String getSourceTag(URI path) throws IOException {
      return getSourceTag(Lib.fileToCharBuffer(new File(path)));
   }

   public void addSourceTag(URI path, String tag) throws IOException {
      File file = new File(path);
      CharBuffer cb = Lib.fileToCharBuffer(file);
      cb = addSourceTag(cb, tag);
      Lib.writeCharBufferToFile(cb, file);
   }

   public void removeSourceTag(URI path) throws IOException {
      File file = new File(path);
      CharBuffer cb = Lib.fileToCharBuffer(file);
      CharBuffer result = removeSourceTag(cb);
      if (!result.equals(cb)) {
         Lib.writeCharBufferToFile(result, file);
      }
   }

   public CharBuffer removeMatches(CharBuffer buffer, Matcher matcher) {
      CharBuffer copy = buffer.duplicate();
      matcher.reset(copy);
      if (matcher.find()) {
         ChangeSet changeSet = new ChangeSet(copy);
         changeSet.delete(matcher.start(), matcher.end() + 1);
         copy = CharBuffer.wrap(changeSet.applyChangesToSelf().toString().toCharArray());
      }
      return copy;
   }

}