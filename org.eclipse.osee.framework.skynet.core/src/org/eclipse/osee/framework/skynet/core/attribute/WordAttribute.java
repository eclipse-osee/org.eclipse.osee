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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.InputStream;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public class WordAttribute extends StringAttribute {
   public static final String WORD_TEMPLATE_CONTENT = "Word Template Content";
   public static final String WHOLE_WORD_CONTENT = "Whole Word Content";
   public static final String OLE_DATA_NAME = "Word Ole Data";
   public static boolean noPopUps = false;
   private static boolean trackedChangesDetected = false;

   public static boolean trackedChangesDetected() {
      return trackedChangesDetected;
   }

   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      // Do not allow save on tracked changes except on three way merges
      if (WordAnnotationHandler.containsWordAnnotations(value) && getArtifact().getBranch().getBranchType() != BranchType.MERGE) {
         trackedChangesDetected = true;
         throw new OseeArgumentException("Tracked changes detected.");
      } else {
         value = WordUtil.removeWordMarkupSmartTags(value);
         return super.subClassSetValue(value);
      }
   }

   public boolean containsWordAnnotations() throws OseeCoreException {
      String temp = getValue();
      return WordAnnotationHandler.containsWordAnnotations(temp);
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      String toReturn = null;
      InputStream inputStream = null;
      try {
         inputStream = new XmlTextInputStream(getValue());
         toReturn = Lib.inputStreamToString(inputStream);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
      return toReturn;
   }

   /**
    * Mainly used for testing purposes
    *
    * @return the noPopUps
    */
   public static boolean isNoPopUps() {
      return noPopUps;
   }

   /**
    * Mainly used for testing purposes
    *
    * @param noPopUps the noPopUps to set
    */
   public static void setNoPopUps(boolean noPopUps) {
      WordAttribute.noPopUps = noPopUps;
   }

   public static void resetTrackedChangesDetection() {
      trackedChangesDetected = false;
   }
}