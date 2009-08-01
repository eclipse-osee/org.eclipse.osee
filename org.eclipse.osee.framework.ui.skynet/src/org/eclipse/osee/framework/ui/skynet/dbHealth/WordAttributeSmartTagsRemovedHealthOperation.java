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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Roberto E. Escobar
 */
public class WordAttributeSmartTagsRemovedHealthOperation extends AbstractWordAttributeHealthOperation {

   public WordAttributeSmartTagsRemovedHealthOperation() {
      super("Word Attribute with Smart Tags");
   }

   @Override
   protected void applyFix(AttrData attrData) throws OseeCoreException {
      String fixedData = WordUtil.removeWordMarkupSmartTags(attrData.getResource().getData());
      attrData.getResource().setData(fixedData);
   }

   @Override
   protected boolean isFixRequired(AttrData attrData, Resource resource) throws OseeCoreException {
      boolean result = false;
      String wordMarkup = resource.getData();
      if (Strings.isValid(wordMarkup)) {
         String[] splitsOnSmartTagStart = wordMarkup.split("<[/]{0,1}st\\d{1,22}");// example smart (cough, cough) tags <st1:place>|</st1:place>
         result = splitsOnSmartTagStart.length > 1;
      }
      return result;
   }

   @Override
   public String getCheckDescription() {
      return "Checks Word Attribute data to detect Smart Tags";
   }

   @Override
   public String getFixDescription() {
      return "Removes smart tags from word attributes";
   }

   @Override
   protected String getBackUpPrefix() {
      return "SmartTagsFix_";
   }

}
