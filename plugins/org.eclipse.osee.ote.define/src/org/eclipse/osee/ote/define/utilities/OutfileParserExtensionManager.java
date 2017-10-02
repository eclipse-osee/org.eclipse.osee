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
package org.eclipse.osee.ote.define.utilities;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.ote.define.OteDefinePlugin;
import org.eclipse.osee.ote.define.parser.BaseOutfileParser;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class OutfileParserExtensionManager {

   private static final String OUTFILE_PARSER = "OutfileParser";
   private static final String CLASS_NAME = "classname";
   private static final String SUPPORTED_EXTENSIONS = "supportedExtensions";
   private final Map<String, BaseOutfileParser> contributions;
   private final HashCollection<String, BaseOutfileParser> extensionsToParsers;

   private static OutfileParserExtensionManager instance = null;

   private OutfileParserExtensionManager() {
      contributions = new HashMap<>();
      extensionsToParsers = new HashCollection<>(false, HashSet.class);
   }

   public static OutfileParserExtensionManager getInstance() {
      if (instance == null) {
         instance = new OutfileParserExtensionManager();
      }
      return instance;
   }

   public String[] getSupportedExtensions()  {
      checkObjectsLoaded();
      Set<String> set = extensionsToParsers.keySet();
      return set.toArray(new String[set.size()]);
   }

   public BaseOutfileParser getOutfileParserFor(URL fileToImport)  {
      checkObjectsLoaded();
      BaseOutfileParser toReturn = null;
      for (BaseOutfileParser parser : getOutfileParsers()) {
         if (parser.isValidParser(fileToImport)) {
            toReturn = parser;
            break;
         }
      }
      if (toReturn == null) {
         throw new OseeStateException("Unsupported outfile type [%s] no valid outfile parser found", fileToImport);
      }
      return toReturn;
   }

   public BaseOutfileParser getOutfileParserById(String id)  {
      checkObjectsLoaded();
      return contributions.get(id);
   }

   public Collection<BaseOutfileParser> getOutfileParsers()  {
      checkObjectsLoaded();
      return contributions.values();
   }

   private void checkObjectsLoaded()  {
      if (contributions.isEmpty()) {
         List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(OteDefinePlugin.PLUGIN_ID + "." + OUTFILE_PARSER, OUTFILE_PARSER);
         for (IConfigurationElement element : elements) {
            IExtension extension = (IExtension) element.getParent();
            String identifier = extension.getUniqueIdentifier();
            String bundleName = element.getContributor().getName();
            String parserClassName = element.getAttribute(CLASS_NAME);
            String supportedExtensions = element.getAttribute(SUPPORTED_EXTENSIONS);
            Set<String> supportedSet = getSupportedExtensions(supportedExtensions);
            BaseOutfileParser parser = (BaseOutfileParser) loadClass(bundleName, parserClassName);
            if (parser != null) {
               contributions.put(identifier, parser);
               for (String extensionType : supportedSet) {
                  extensionsToParsers.put(extensionType, parser);
               }
            }
         }
      }
   }

   private Set<String> getSupportedExtensions(String rawExtensions) {
      Set<String> toReturn = new HashSet<>();
      for (String value : rawExtensions.split(";")) {
         value = value.trim();
         if (Strings.isValid(value)) {
            toReturn.add(value);
         }
      }
      return toReturn;
   }

   private Object loadClass(String bundleName, String className)  {
      Object object = null;
      if (Strings.isValid(bundleName) && Strings.isValid(className)) {
         try {
            Bundle bundle = Platform.getBundle(bundleName);
            Class<?> taskClass = bundle.loadClass(className);
            try {
               Method getInstance = taskClass.getMethod("getInstance", new Class[] {});
               object = getInstance.invoke(null, new Object[] {});
            } catch (Exception ex) {
               object = taskClass.newInstance();
            }
         } catch (Exception ex) {
            throw new OseeCoreException(String.format("Unable to Load: [%s - %s]", bundleName, className), ex);
         }
      }
      return object;
   }
}
