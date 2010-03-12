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

import java.util.List;
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
    * @param options
    * @return match location
    */
   public List<MatchLocation> find(AttributeData attributeData, String toSearch, SearchOptions options) throws Exception;
}
