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

import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.internal.TagProcessor;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;

/**
 * @author Roberto E. Escobar
 */
public class XmlAttributeTaggerProvider extends BaseAttributeTaggerProvider {

   private static final Pattern tagKiller = Pattern.compile("<.*?>", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern = Pattern.compile("<w:p( .*?)?>");

   private String removeWordMLTags(String str) {
      str = paragraphPattern.matcher(Xml.unescape(str)).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return str;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.attribute.IAttributeTagger#find(org.eclipse.osee.framework.search.engine.attribute.AttributeData, java.lang.String)
    */
   @Override
   public boolean find(AttributeData attributeData, String value) {
      boolean toReturn = false;
      if (Strings.isValid(value)) {
         value = value.toLowerCase();
         toReturn = removeWordMLTags(getValue(attributeData)).toLowerCase().contains(value);
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.attribute.IAttributeTagger#tagIt(org.eclipse.osee.framework.search.engine.attribute.AttributeData, org.eclipse.osee.framework.search.engine.utility.ITagCollector)
    */
   @Override
   public void tagIt(AttributeData attributeData, ITagCollector collector) throws Exception {
      TagProcessor.collectFromString(removeWordMLTags(getValue(attributeData)), collector);
   }
}
