/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ReviewFormalTypeColumn extends XViewerAtsAttributeValueColumn {

   public static ReviewFormalTypeColumn instance = new ReviewFormalTypeColumn();

   public static ReviewFormalTypeColumn getInstance() {
      return instance;
   }

   private ReviewFormalTypeColumn() {
      super(AtsAttributeTypes.ReviewFormalType, 22, SWT.CENTER, true, SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ReviewFormalTypeColumn copy() {
      ReviewFormalTypeColumn newXCol = new ReviewFormalTypeColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   public static ReviewFormalType getReviewFormalType(Artifact artifact) throws OseeCoreException {
      String value = artifact.getSoleAttributeValue(AtsAttributeTypes.ReviewFormalType, "");
      if (Strings.isValid(value)) {
         try {
            return ReviewFormalType.valueOf(value);
         } catch (IllegalArgumentException ex) {
            OseeLog.logf(Activator.class, Level.SEVERE, ex, "Unexpected formal type [%s]", value);
         }
      }
      return null;
   }
}
