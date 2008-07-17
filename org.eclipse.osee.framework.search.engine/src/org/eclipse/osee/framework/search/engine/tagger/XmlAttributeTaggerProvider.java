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
package org.eclipse.osee.framework.search.engine.tagger;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.internal.TagProcessor;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.WordsUtil;

/**
 * @author Roberto E. Escobar
 */
public class XmlAttributeTaggerProvider extends BaseAttributeTaggerProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.attribute.IAttributeTagger#find(org.eclipse.osee.framework.search.engine.attribute.AttributeData, java.lang.String)
    */
   @Override
   public boolean find(AttributeData attributeData, String value) {
      boolean toReturn = false;
      if (Strings.isValid(value)) {
         value = value.toLowerCase();
         toReturn = WordsUtil.extractTextDataFromXMLTags(getValue(attributeData)).toLowerCase().contains(value);
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.attribute.IAttributeTagger#tagIt(org.eclipse.osee.framework.search.engine.attribute.AttributeData, org.eclipse.osee.framework.search.engine.utility.ITagCollector)
    */
   @Override
   public void tagIt(AttributeData attributeData, ITagCollector collector) throws Exception {
      TagProcessor.collectFromString(WordsUtil.extractTextDataFromXMLTags(getValue(attributeData)), collector);
   }
}
