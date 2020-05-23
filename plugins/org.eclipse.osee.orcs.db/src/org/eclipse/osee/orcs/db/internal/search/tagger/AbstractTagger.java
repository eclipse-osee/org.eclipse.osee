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
