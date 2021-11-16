/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.search;

import org.eclipse.osee.ats.api.util.AtsImage;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchWorkflowAdvSearchItem extends AtsSearchWorkflowSearchItem {

   private static final String TITLE = "Action Search (Advanced)";

   public AtsSearchWorkflowAdvSearchItem() {
      super(TITLE, IMAGE);
   }

   public AtsSearchWorkflowAdvSearchItem(AtsSearchWorkflowSearchItem searchItem) {
      this(searchItem, TITLE, IMAGE);
   }

   public AtsSearchWorkflowAdvSearchItem(AtsSearchWorkflowSearchItem searchItem, String name, AtsImage image) {
      super(searchItem, name, image);
      setShortName(name);
   }

   @Override
   protected boolean isAdvanced() {
      return true;
   }

   @Override
   public AtsSearchWorkflowSearchItem copy() {
      AtsSearchWorkflowAdvSearchItem item = new AtsSearchWorkflowAdvSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AtsSearchWorkflowSearchItem copyProvider() {
      AtsSearchWorkflowAdvSearchItem item = new AtsSearchWorkflowAdvSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

}
