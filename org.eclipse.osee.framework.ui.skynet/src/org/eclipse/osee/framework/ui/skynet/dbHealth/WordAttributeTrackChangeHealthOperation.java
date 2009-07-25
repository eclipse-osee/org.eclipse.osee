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

import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;

/**
 * @author Roberto E. Escobar
 */
public class WordAttributeTrackChangeHealthOperation extends AbstractWordAttributeHealthOperation {

   public WordAttributeTrackChangeHealthOperation() {
      super("Word Attribute Track Change Enabled");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#getDescription()
    */
   @Override
   public String getCheckDescription() {
      return "Checks Word Attribute data to detect word track changes";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#getFixDescription()
    */
   @Override
   public String getFixDescription() {
      return "Removes track changes from word attributes";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.AbstractWordAttributeHealthOperation#applyFix(org.eclipse.osee.framework.ui.skynet.dbHealth.AbstractWordAttributeHealthOperation.AttrData)
    */
   @Override
   protected void applyFix(AttrData attrData) {
      String fixedData = WordAnnotationHandler.removeAnnotations(attrData.getResource().getData());
      attrData.getResource().setData(fixedData);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.AbstractWordAttributeHealthOperation#getBackUpPrefix()
    */
   @Override
   protected String getBackUpPrefix() {
      return "TrackChangesFix_";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.AbstractWordAttributeHealthOperation#isFixRequired(org.eclipse.osee.framework.ui.skynet.dbHealth.AbstractWordAttributeHealthOperation.AttrData, org.eclipse.osee.framework.ui.skynet.dbHealth.AbstractWordAttributeHealthOperation.Resource)
    */
   @Override
   protected boolean isFixRequired(AttrData attrData, Resource resource) {
      return WordAnnotationHandler.containsWordAnnotations(resource.getData());
   }
}
