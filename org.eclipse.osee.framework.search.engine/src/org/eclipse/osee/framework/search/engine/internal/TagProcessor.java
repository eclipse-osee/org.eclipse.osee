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
package org.eclipse.osee.framework.search.engine.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagEncoder;
import org.eclipse.osee.framework.search.engine.utility.WordChunker;
import org.eclipse.osee.framework.search.engine.utility.WordsUtil;

/**
 * @author Roberto E. Escobar
 */
public class TagProcessor {

   public static void collectFromString(String value, ITagCollector tagCollector) throws UnsupportedEncodingException {
      collectFromInputStream(new ByteArrayInputStream(value.getBytes("UTF-8")), tagCollector);
   }

   public static void collectFromInputStream(InputStream inputStream, ITagCollector tagCollector) throws UnsupportedEncodingException {
      WordChunker chunker = new WordChunker(inputStream);
      for (String original : chunker) {
         String target = WordsUtil.toSingular(WordsUtil.stripPossesive(original));
         TagEncoder.encode(target, tagCollector);
         for (String toEncode : WordsUtil.splitOnPunctuation(target)) {
            TagEncoder.encode(toEncode, tagCollector);
         }
      }
   }
}
