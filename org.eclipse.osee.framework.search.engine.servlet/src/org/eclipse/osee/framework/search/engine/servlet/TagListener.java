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
package org.eclipse.osee.framework.search.engine.servlet;

import org.eclipse.osee.framework.search.engine.ITagListener;

/**
 * @author Roberto E. Escobar
 */
public class TagListener implements ITagListener {

   private int queryId;

   public TagListener(int queryId) {
      this.queryId = queryId;
   }

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
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagQueryIdTagComplete(int, long, long)
    */
   @Override
   synchronized public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
      if (this.queryId == queryId) {
         this.notify();
      }
   }

}
