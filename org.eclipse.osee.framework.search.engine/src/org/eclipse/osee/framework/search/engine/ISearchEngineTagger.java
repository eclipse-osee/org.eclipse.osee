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
    * Create tags for a particular branch.
    * 
    * @param branchId of branch to tag
    */
   public void tagByBranchId(int branchId);

   /**
    * Create tags for a particular branch. Notifies listener of tagging events.
    * 
    * @param listener object listening for tag events
    * @param branchId of branch to tag
    */
   public void tagByBranchId(ITagListener listener, int queryId);

   /**
    * Create tags for queue query id.
    * 
    * @param queryId queryId to tag
    */
   public void tagByQueueQueryId(int queryId);

   /**
    * Create tags for queue query id. Notifies listener of tagging events.
    * 
    * @param listener object listening for tag events
    * @param queryId queryId to tag
    */
   public void tagByQueueQueryId(ITagListener listener, int queryId);

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
   public void tagFromXmlStream(InputStream inputStream) throws Exception;

   /**
    * Create tags for attributes specified in xml stream. Notifies listener of tagging events. <b>
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
    * @param listener object listening for tag events
    * @param inputStream xml inputStream
    */
   public void tagFromXmlStream(ITagListener listener, InputStream inputStream) throws Exception;

   /**
    * Stop tagging items by queue query id
    * 
    * @param queryId
    * @return number of items stopped
    */
   public int stopTaggingByQueueQueryId(int... queryId);

   /**
    * Stops all tagging
    * 
    * @return number of items stopped
    */
   public int stopAllTagging();

   /**
    * Delete tags specified by join query id
    * 
    * @param parseInt
    */
   public int deleteTags(int joinQueryId) throws Exception;

   /**
    * Get number of workers waiting to execute tagging operation
    * 
    * @return number of workers waiting to tag
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

}
