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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactSourceParser;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactSourceParserDelegate;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactSourceParserContributionManager {

   private static final String PARSER_ELEMENT = "ArtifactSourceParser";
   private static final String PARSER_DELEGATE_ELEMENT = "ArtifactSourceParserDelegate";
   private static final String PARSER_EXTENSION = Activator.PLUGIN_ID + "." + PARSER_ELEMENT;
   private static final String PARSER_DELEGATE_EXTENSION = Activator.PLUGIN_ID + "." + PARSER_DELEGATE_ELEMENT;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";

   public ArtifactSourceParserContributionManager() {
   }

   public List<IArtifactSourceParser> getArtifactSourceParser() {
      ExtensionDefinedObjects<IArtifactSourceParser> definedObjects =
            new ExtensionDefinedObjects<IArtifactSourceParser>(PARSER_EXTENSION, PARSER_ELEMENT, CLASS_NAME_ATTRIBUTE,
                  true);
      List<IArtifactSourceParser> items = definedObjects.getObjects();
      Collections.sort(items, new ParserComparator());
      return items;
   }

   public List<IArtifactSourceParserDelegate> getArtifactSourceParserDelegate(IArtifactSourceParser parser) {
      List<IArtifactSourceParserDelegate> contentHandlers = new ArrayList<IArtifactSourceParserDelegate>();
      if (parser != null) {
         ExtensionDefinedObjects<IArtifactSourceParserDelegate> contributions =
               new ExtensionDefinedObjects<IArtifactSourceParserDelegate>(PARSER_DELEGATE_EXTENSION,
                     PARSER_DELEGATE_ELEMENT, CLASS_NAME_ATTRIBUTE, true);
         for (IArtifactSourceParserDelegate delegate : contributions.getObjects()) {
            if (delegate.isApplicable(parser)) {
               contentHandlers.add(delegate);
            }
         }
         Collections.sort(contentHandlers, new ParserDelegateComparator());
      }
      return contentHandlers;
   }

   private static final class ParserDelegateComparator implements Comparator<IArtifactSourceParserDelegate> {

      public int compare(IArtifactSourceParserDelegate o1, IArtifactSourceParserDelegate o2) {
         return o1.getName().compareToIgnoreCase(o2.getName());
      }
   };

   private static final class ParserComparator implements Comparator<IArtifactSourceParser> {

      public int compare(IArtifactSourceParser o1, IArtifactSourceParser o2) {
         return o1.getName().compareToIgnoreCase(o2.getName());
      }
   };
}
