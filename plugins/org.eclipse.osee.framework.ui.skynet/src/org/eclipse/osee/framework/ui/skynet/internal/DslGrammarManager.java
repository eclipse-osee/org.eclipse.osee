/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.internal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.DslGrammar;

/**
 * @author Roberto E. Escobar
 */
public final class DslGrammarManager {

   private static final DslGrammarRegistryProvider registryProvider = new DslGrammarRegistryProvider();

   private DslGrammarManager() {
      // Utility
   }

   public static void invalidateRegistry() {
      registryProvider.invalidate();
   }

   public static DslGrammar getDslByExtension(final String extension) {
      Conditions.checkNotNull(extension, "extension");
      return registryProvider.get().getGrammarByExtension(extension);
   }

   public static DslGrammar getDslByGrammarId(final String grammarId) {
      Conditions.checkNotNull(grammarId, "grammarId");
      return registryProvider.get().getGrammarById(grammarId);
   }

   public static boolean isGrammarAvailable(String grammarId) {
      boolean result = false;
      try {
         result = registryProvider.get().isGrammarAvailable(grammarId);
      } catch (OseeCoreException ex) {
         // Do nothing
      }
      return result;
   }

   public static boolean isDslGrammarExtension(String extension) {
      boolean result = false;
      try {
         result = registryProvider.get().doesExtensionHaveDslGrammar(extension);
      } catch (OseeCoreException ex) {
         // Do nothing
      }
      return result;
   }

   public static boolean isDslAttributeType(AttributeTypeId attributeType) {
      boolean result = false;
      try {
         AttributeType type = AttributeTypeManager.getType(attributeType);
         String mediaType = type.getMediaType();
         result = Strings.isValid(mediaType) && mediaType.toLowerCase().endsWith("dsl");
      } catch (OseeCoreException ex) {
         // Do nothing
      }
      return result;
   }

   public static DslGrammar getGrammar(AttributeTypeId attributeType) {
      Conditions.checkNotNull(attributeType, "attributeType");

      DslGrammar toReturn = null;
      AttributeType type = AttributeTypeManager.getType(attributeType);
      String mediaType = type.getMediaType();
      if (Strings.isValid(mediaType)) {
         mediaType = mediaType.toLowerCase();
         mediaType = mediaType.replaceAll("application/", "");
         mediaType = mediaType.replaceAll("\\+dsl", "");
         toReturn = getDslByExtension(mediaType);
      }
      return toReturn;
   }

   private static final class DslGrammarRegistry {

      private final ImmutableMap<String, DslGrammar> idToGrammar;
      private final ImmutableMap<String, DslGrammar> extensionToGrammar;

      public DslGrammarRegistry(ImmutableMap<String, DslGrammar> idToGrammar, ImmutableMap<String, DslGrammar> extensionToGrammar) {
         super();
         this.idToGrammar = idToGrammar;
         this.extensionToGrammar = extensionToGrammar;
      }

      public DslGrammar getGrammarById(String grammarId) {
         return idToGrammar.get(grammarId);
      }

      public boolean isGrammarAvailable(String grammarId) {
         return idToGrammar.containsKey(grammarId);
      }

      public boolean doesExtensionHaveDslGrammar(String extension) {
         return extensionToGrammar.containsKey(extension);
      }

      public DslGrammar getGrammarByExtension(String extension) {
         return extensionToGrammar.get(extension);
      }
   }

   private static final class DslGrammarRegistryProvider extends LazyObject<DslGrammarRegistry> {
      private static final String EXTENSION_ELEMENT_NAME = "DslGrammar";
      private static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID + "." + EXTENSION_ELEMENT_NAME;
      private static final String EXTENSION_CLASS = "classname";

      @Override
      protected FutureTask<DslGrammarRegistry> createLoaderTask() {
         Callable<DslGrammarRegistry> callable = new Callable<DslGrammarRegistry>() {

            @Override
            public DslGrammarRegistry call() throws Exception {
               ExtensionDefinedObjects<DslGrammar> extensions =
                  new ExtensionDefinedObjects<>(EXTENSION_POINT_ID, EXTENSION_ELEMENT_NAME, EXTENSION_CLASS);

               List<DslGrammar> grammars = extensions.getObjects();
               ImmutableMap<String, DslGrammar> idToGrammar =
                  Maps.uniqueIndex(grammars, new Function<DslGrammar, String>() {
                     @Override
                     public String apply(DslGrammar grammar) {
                        return grammar.getGrammarId();
                     }
                  });
               ImmutableMap<String, DslGrammar> extensionToGrammar =
                  Maps.uniqueIndex(grammars, new Function<DslGrammar, String>() {
                     @Override
                     public String apply(DslGrammar grammar) {
                        return grammar.getExtension();
                     }
                  });
               return new DslGrammarRegistry(idToGrammar, extensionToGrammar);
            }

         };
         return new FutureTask<>(callable);
      }

   }

}