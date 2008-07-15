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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.search.engine.Activator;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.internal.TagProcessor;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.WordsUtil;

/**
 * @author Roberto E. Escobar
 */
public class WordAttributeTaggerProvider implements IAttributeTaggerProvider {

   private static final Pattern tagKiller = Pattern.compile("<.*?>", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern = Pattern.compile("<w:p( .*?)?>");

   private static String removeWordMLTags(String str) {
      str = paragraphPattern.matcher(Xml.unescape(str)).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return str;
   }

   public String getValue(AttributeData attributeData) {
      String toReturn = null;
      if (attributeData.isUriValid()) {
         InputStream inputStream = null;
         try {
            Options options = new Options();
            options.put(StandardOptions.DecompressOnAquire.name(), true);
            IResourceLocator locator =
                  Activator.getInstance().getResourceLocatorManager().getResourceLocator(attributeData.getUri());
            IResource resource = Activator.getInstance().getResourceManager().acquire(locator, options);

            inputStream = resource.getContent();
            toReturn = Lib.inputStreamToString(inputStream);

         } catch (Exception ex) {
            OseeLog.log(WordAttributeTaggerProvider.class, Level.SEVERE, ex.toString(), ex);
         } finally {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (IOException ex) {
                  OseeLog.log(WordAttributeTaggerProvider.class, Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      }
      return Strings.isValid(toReturn) ? toReturn : WordsUtil.EMPTY_STRING;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.attribute.IAttributeTagger#find(org.eclipse.osee.framework.search.engine.attribute.AttributeData, java.lang.String)
    */
   @Override
   public boolean find(AttributeData attributeData, String value) {
      return removeWordMLTags(getValue(attributeData)).contains(value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.attribute.IAttributeTagger#tagIt(org.eclipse.osee.framework.search.engine.attribute.AttributeData, org.eclipse.osee.framework.search.engine.utility.ITagCollector)
    */
   @Override
   public void tagIt(AttributeData attributeData, ITagCollector collector) throws Exception {
      TagProcessor.collectFromString(removeWordMLTags(getValue(attributeData)), collector);
   }

}
