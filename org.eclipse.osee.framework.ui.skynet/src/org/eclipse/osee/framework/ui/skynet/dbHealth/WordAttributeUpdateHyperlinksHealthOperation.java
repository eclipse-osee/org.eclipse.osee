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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler.MatchRange;

/**
 * @author Roberto E. Escobar
 */
public class WordAttributeUpdateHyperlinksHealthOperation extends AbstractWordAttributeHealthOperation {

   private final OseeLinkBuilder linkBuilder;

   public WordAttributeUpdateHyperlinksHealthOperation() {
      super("Word Attribute Old style hyperlinks");
      linkBuilder = new OseeLinkBuilder();
   }

   @Override
   protected void applyFix(AttrData attrData) throws OseeCoreException {
      String original = attrData.getResource().getData();
      HashCollection<String, MatchRange> matches = WordMlLinkHandler.parseOseeWordMLLinks(original);
      String converted = convertWordMlLinks(original, matches);
      attrData.getResource().setData(converted);
   }

   @Override
   protected boolean isFixRequired(AttrData attrData, Resource resource) throws OseeCoreException {
      boolean result = false;
      String content = resource.getData();
      if (Strings.isValid(content)) {
         result = !WordMlLinkHandler.parseOseeWordMLLinks(content).isEmpty();
      }
      return result;
   }

   private String convertWordMlLinks(String original, HashCollection<String, MatchRange> matches) {
      ChangeSet changeSet = new ChangeSet(original);
      for (String guid : matches.keySet()) {
         Collection<MatchRange> matchRanges = matches.getValues(guid);
         if (matchRanges != null) {
            for (MatchRange match : matchRanges) {
               String replaceWith = linkBuilder.getOseeLinkMarker(guid);
               changeSet.replace(match.start(), match.end(), replaceWith);
            }
         }
      }
      return changeSet.applyChangesToSelf().toString();
   }

   @Override
   public String getCheckDescription() {
      return "Checks Word Attribute data to detect old style hyperlinks";
   }

   @Override
   public String getFixDescription() {
      return "Converts old style hyperlinks to new style";
   }

   @Override
   protected String getBackUpPrefix() {
      return "HyperlinkFix_";
   }
}
