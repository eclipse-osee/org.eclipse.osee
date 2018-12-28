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
package org.eclipse.osee.framework.skynet.core.importing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactExtractorContributionManager {

   private static final String PARSER_ELEMENT = "ArtifactExtractor";
   private static final String PARSER_DELEGATE_ELEMENT = "ArtifactExtractorDelegate";
   private static final String PARSER_EXTENSION = Activator.PLUGIN_ID + "." + PARSER_ELEMENT;
   private static final String PARSER_DELEGATE_EXTENSION = Activator.PLUGIN_ID + "." + PARSER_DELEGATE_ELEMENT;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";

   public List<IArtifactExtractor> getExtractors() {
      ExtensionDefinedObjects<IArtifactExtractor> definedObjects =
         new ExtensionDefinedObjects<>(PARSER_EXTENSION, PARSER_ELEMENT, CLASS_NAME_ATTRIBUTE, true);
      List<IArtifactExtractor> items = definedObjects.getObjects();
      Collections.sort(items, new ParserComparator());
      return items;
   }

   public List<IArtifactExtractorDelegate> getAllDelegates() {
      List<IArtifactExtractorDelegate> contentHandlers = new ArrayList<>();
      ExtensionDefinedObjects<IArtifactExtractorDelegate> contributions =
         new ExtensionDefinedObjects<>(PARSER_DELEGATE_EXTENSION, PARSER_DELEGATE_ELEMENT,
            CLASS_NAME_ATTRIBUTE, true);
      for (IArtifactExtractorDelegate delegate : contributions.getObjects()) {
         contentHandlers.add(delegate);
      }
      return contentHandlers;
   }

   public List<IArtifactExtractorDelegate> getDelegates(IArtifactExtractor parser) {
      List<IArtifactExtractorDelegate> contentHandlers = new ArrayList<>();
      if (parser != null) {
         for (IArtifactExtractorDelegate delegate : getAllDelegates()) {
            if (delegate.isApplicable(parser)) {
               contentHandlers.add(delegate);
            }
         }
         Collections.sort(contentHandlers, new ParserDelegateComparator());
      }
      return contentHandlers;
   }

   private static final class ParserDelegateComparator implements Comparator<IArtifactExtractorDelegate>, Serializable {
      private static final long serialVersionUID = 803641362587689953L;

      @Override
      public int compare(IArtifactExtractorDelegate o1, IArtifactExtractorDelegate o2) {
         return o1.getName().compareToIgnoreCase(o2.getName());
      }
   };

   private static final class ParserComparator implements Comparator<IArtifactExtractor>, Serializable {
      private static final long serialVersionUID = 297281600323509207L;

      @Override
      public int compare(IArtifactExtractor o1, IArtifactExtractor o2) {
         return o1.getName().compareToIgnoreCase(o2.getName());
      }
   };
}
