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
package org.eclipse.osee.framework.search.engine;

/**
 * @author Roberto E. Escobar
 */
public class TagListenerAdapter implements ITagListener {

   @Override
   public void onAttributeAddTagEvent(int queryId, long gammaId, String word, long codedTag) {
      // Default implementation
   }

   @Override
   public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
      // Default implementation
   }

   @Override
   public void onTagQueryIdSubmit(int queryId) {
      // Default implementation
   }

   @Override
   public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
      // Default implementation
   }

   @Override
   public void onTagExpectedQueryIdSubmits(int totalQueries) {
      // Default implementation
   }

   @Override
   public void onTagError(int queryId, Throwable throwable) {
      // Default implementation
   }
}
