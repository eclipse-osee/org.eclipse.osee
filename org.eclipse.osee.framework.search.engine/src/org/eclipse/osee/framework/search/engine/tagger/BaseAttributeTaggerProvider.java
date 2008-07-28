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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.search.engine.Activator;
import org.eclipse.osee.framework.search.engine.IAttributeTaggerProvider;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.utility.WordsUtil;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseAttributeTaggerProvider implements IAttributeTaggerProvider {

   protected String getValue(AttributeData attributeData) {
      String value = getExtendedData(attributeData);
      if (value == null) {
         value = attributeData.getStringValue();
      }
      return Strings.isValid(value) ? value : WordsUtil.EMPTY_STRING;
   }

   protected InputStream getValueAsStream(AttributeData attributeData) throws Exception {
      InputStream inputStream = getExtendedDataAsStream(attributeData);
      if (inputStream == null) {
         inputStream = new ByteArrayInputStream(attributeData.getStringValue().getBytes("UTF-8"));
      }
      return inputStream;
   }

   private InputStream getExtendedDataAsStream(AttributeData attributeData) throws Exception {
	  InputStream toReturn = null;
	  if (attributeData.isUriValid()) {
         Options options = new Options();
         options.put(StandardOptions.DecompressOnAquire.name(), true);
         IResourceLocator locator =
            Activator.getInstance().getResourceLocatorManager().getResourceLocator(attributeData.getUri());
         IResource resource = Activator.getInstance().getResourceManager().acquire(locator, options);
         toReturn = resource.getContent();
	  }
	  return toReturn;
   }

   private String getExtendedData(AttributeData attributeData) {
      String toReturn = null;
      if (attributeData.isUriValid()) {
         InputStream inputStream = null;
         try {
            inputStream = getExtendedDataAsStream(attributeData);
            toReturn = Lib.inputStreamToString(inputStream);
         } catch (Exception ex) {
            OseeLog.log(XmlAttributeTaggerProvider.class, Level.SEVERE, ex.toString(), ex);
         } finally {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (IOException ex) {
                  OseeLog.log(XmlAttributeTaggerProvider.class, Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      }
      return toReturn;
   }
}
