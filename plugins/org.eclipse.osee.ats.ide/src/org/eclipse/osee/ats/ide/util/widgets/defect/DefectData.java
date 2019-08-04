/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets.defect;

import java.util.Collection;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.ide.workflow.review.defect.ReviewDefectError;

/**
 * @author Donald G. Dunne
 */
public class DefectData {

   Collection<ReviewDefectItem> defectItems;
   ReviewDefectError error;

   public DefectData() {
   }

   public Collection<ReviewDefectItem> getDefectItems() {
      return defectItems;
   }

   public void setDefectItems(Collection<ReviewDefectItem> defectItems) {
      this.defectItems = defectItems;
   }

   public ReviewDefectError getError() {
      return error;
   }

   public void setError(ReviewDefectError error) {
      this.error = error;
   }

}
