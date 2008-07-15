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

import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;

/**
 * @author Roberto E. Escobar
 */
public interface IAttributeTaggerProviderManager {

   /**
    * Add attribute tagger provider
    * 
    * @param attributeTaggerProvider
    */
   public void addAttributeTaggerProvider(IAttributeTaggerProvider attributeTaggerProvider);

   /**
    * Remove attribute tagger provider
    * 
    * @param attributeTaggerProvider
    */
   public void removeAttributeTaggerProvider(IAttributeTaggerProvider attributeTaggerProvider);

   /**
    * Creates tags for attribute content
    * 
    * @param attributeData attribute to content to tag
    * @param collector object collecting tags
    * @throws Exception
    */
   public void tagIt(AttributeData attributeData, ITagCollector collector) throws Exception;

   /**
    * Searches attribute content for value match
    * 
    * @param attributeData attribute to search in
    * @param value to search in attribute content
    * @return <b>true</b> if value is found in attribute content. <b>false</b> if value is not found in attribute
    *         content
    */
   public boolean find(AttributeData attributeData, String value) throws Exception;
}
