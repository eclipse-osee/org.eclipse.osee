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
   int getWorkersInQueue();
}
