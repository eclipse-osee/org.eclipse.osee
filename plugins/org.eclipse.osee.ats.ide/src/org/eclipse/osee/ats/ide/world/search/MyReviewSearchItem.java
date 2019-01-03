/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.world.search;

import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.search.WorldSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class MyReviewSearchItem extends WorldSearchItem {

   public MyReviewSearchItem() {
      super("My Reviews");
   }

   @Override
   public WorldUISearchItem copy() {
      AtsSearchData data = getData();
      if (!Strings.isValid(data.getUserId())) {
         data.getWorkItemTypes().add(WorkItemType.Review);
         data.setUserId(AtsClientService.get().getUserService().getCurrentUserId());
         data.setUserType(AtsSearchUserType.Assignee);
      }
      return new WorldSearchItem(data);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.REVIEW);
   }

}
