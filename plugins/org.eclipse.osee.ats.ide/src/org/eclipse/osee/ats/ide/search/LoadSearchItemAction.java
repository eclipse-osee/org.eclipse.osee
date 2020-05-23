/*********************************************************************
 * Copyright (c) 2015 Boeing
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.query.AtsSearchData;

/**
 * @author Donald G. Dunne
 */
public final class LoadSearchItemAction extends Action {

   private final AtsSearchWorkflowSearchItem searchItem;
   private final AtsSearchData data;

   public LoadSearchItemAction(AtsSearchData data, AtsSearchWorkflowSearchItem searchItem) {
      this.data = data;
      this.searchItem = searchItem;
   }

   @Override
   public String getText() {
      return data.getSearchName();
   }

   @Override
   public void run() {
      searchItem.loadWidgets(data);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return searchItem.getImageDescriptor();
   }

};
