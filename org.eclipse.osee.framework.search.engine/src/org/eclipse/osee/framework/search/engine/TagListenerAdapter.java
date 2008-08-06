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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onAttributeAddTagEvent(int, long, java.lang.String, long)
    */
   @Override
   public void onAttributeAddTagEvent(int queryId, long gammaId, String word, long codedTag) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onAttributeTagComplete(int, long, int, long)
    */
   @Override
   public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagQueryIdSubmit(int)
    */
   @Override
   public void onTagQueryIdSubmit(int queryId) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagQueryIdTagComplete(int, long, long)
    */
   @Override
   public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagExpectedQueryIdSubmits(int)
    */
   @Override
   public void onTagExpectedQueryIdSubmits(int totalQueries) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagError(int, java.lang.Throwable)
    */
   @Override
   public void onTagError(int queryId, Throwable throwable) {
   }
}
