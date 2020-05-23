/*********************************************************************
 * Copyright (c) 2016 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets.defect;

import java.util.Collection;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.core.review.ReviewDefectError;

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
