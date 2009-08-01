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
   }

   @Override
   public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
   }

   @Override
   public void onTagQueryIdSubmit(int queryId) {
   }

   @Override
   public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
   }

   @Override
   public void onTagExpectedQueryIdSubmits(int totalQueries) {
   }

   @Override
   public void onTagError(int queryId, Throwable throwable) {
   }
}
