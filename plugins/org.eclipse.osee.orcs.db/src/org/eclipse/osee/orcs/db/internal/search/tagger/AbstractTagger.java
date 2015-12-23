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

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractTagger implements Tagger {

   private final StreamMatcher matcher;
   private final TagProcessor tagProcessor;

   protected AbstractTagger(TagProcessor tagProcessor, StreamMatcher matcher) {
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

}
