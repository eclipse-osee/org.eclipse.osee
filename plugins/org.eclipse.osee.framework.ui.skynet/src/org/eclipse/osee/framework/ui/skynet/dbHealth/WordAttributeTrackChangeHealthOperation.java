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
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;

/**
 * @author Roberto E. Escobar
 */
public class WordAttributeTrackChangeHealthOperation extends AbstractWordAttributeHealthOperation {

   public WordAttributeTrackChangeHealthOperation() {
      super("Word Attribute Track Change Enabled");
   }

   @Override
   public String getCheckDescription() {
      return "Checks Word Attribute data to detect word track changes";
   }

   @Override
   public String getFixDescription() {
      return "Removes track changes from word attributes";
   }

   @Override
   protected void applyFix(AttrData attrData) throws OseeCoreException {
      String fixedData = WordAnnotationHandler.removeAnnotations(attrData.getResource().getData());
      attrData.getResource().setData(fixedData);
   }

   @Override
   protected String getBackUpPrefix() {
      return "TrackChangesFix_";
   }

   @Override
   protected boolean isFixRequired(AttrData attrData, Resource resource) throws OseeCoreException {
      boolean result = false;
      String data = resource.getData();
      if (Strings.isValid(data)) {
         result = WordAnnotationHandler.containsWordAnnotations(data);
      }
      return result;
   }
}
