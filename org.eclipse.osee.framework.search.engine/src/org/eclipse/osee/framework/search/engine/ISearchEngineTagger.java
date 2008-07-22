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

import java.io.InputStream;

/**
 * @author Roberto E. Escobar
 */
public interface ISearchEngineTagger {

   /**
    * Create tags for attribute with gamma id.
    * 
    * @param gammaId attribute to tag
    */
   public void tagAttribute(long gammaId);

   /**
    * Create tags for attribute with gamma id. Notifies listener once tagging is complete.
    * 
    * @param listener object listening for tag events
    * @param gammaId attribute to tag
    */
   public void tagAttribute(ITagListener listener, long gammaId);

   /**
    * Create tags for attributes specified in xml stream. <b>
    * 
    * <pre>
    * The XML data is formatted as follows:
    *    &lt;AttributeTag&gt;
    *       &lt;entry gammaId=&quot;90&quot;/&gt;
    *       &lt;entry gammaId=&quot;91&quot;/&gt;
    *                .
    *                .
    *                .
    *    &lt;AttributeTag&gt;
    * </pre>
    * 
    * </b>
    * 
    * @param inputStream xml inputStream
    */
   public void tagFromXmlStream(InputStream inputStream);

   /**
    * Get number of items waiting to be tagged
    * 
    * @return number of items waiting to be tagged
    */
   public int getWorkersInQueue();

   /**
    * Get statistics
    * 
    * @return tagger statistics
    */
   public ITaggerStatistics getStatistics();

   /**
    * Clear Statistics
    */
   public void clearStatistics();

   /**
    * Delete tags specified by join query id
    * 
    * @param parseInt
    */
   public int deleteTags(int joinQueryId) throws Exception;
}
