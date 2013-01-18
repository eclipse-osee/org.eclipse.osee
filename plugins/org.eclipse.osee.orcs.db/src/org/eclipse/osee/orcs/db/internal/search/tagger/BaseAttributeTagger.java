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
package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseAttributeTagger implements Tagger {

   private final StreamMatcher matcher;
   private final TagProcessor tagProcessor;

   protected BaseAttributeTagger(TagProcessor tagProcessor, StreamMatcher matcher) {
      super();
      this.tagProcessor = tagProcessor;
      this.matcher = matcher;
   }

   protected TagProcessor getTagProcessor() {
      return tagProcessor;
   }

   protected StreamMatcher getMatcher() {
      return matcher;
   }

   protected InputStream getValueAsStream(AttributeReadable<?> attribute) throws OseeCoreException {
      String content = String.valueOf(attribute.getValue());
      InputStream inputStream = null;
      try {
         inputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return inputStream;
   }

}
