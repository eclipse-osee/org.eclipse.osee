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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.AtsImage;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchTaskSearchItem extends AbstractWorkItemSearchItem {

   private static final AtsImage IMAGE = AtsImage.TASK;
   private static final String TITLE = "Task Search";
   public static final String NAMESPACE = AtsSearchUtil.ATS_QUERY_TASK_NAMESPACE;

   public AtsSearchTaskSearchItem() {
      super(TITLE, AtsSearchUtil.ATS_QUERY_TASK_NAMESPACE, IMAGE);
   }

   public AtsSearchTaskSearchItem(AbstractWorkItemSearchItem searchItem) {
      super(searchItem, TITLE, AtsSearchUtil.ATS_QUERY_TASK_NAMESPACE, IMAGE);
   }

   public AtsSearchTaskSearchItem(String title, String namespace, AtsImage image) {
      super(title, namespace, image);
   }

   @Override
   public AbstractWorkItemSearchItem copy() {
      AtsSearchTaskSearchItem item = new AtsSearchTaskSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AbstractWorkItemSearchItem copyProvider() {
      AtsSearchTaskSearchItem item = new AtsSearchTaskSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public String getShortNamePrefix() {
      return "TSKS";
   }

   @Override
   Collection<WorkItemType> getWorkItemTypes() {
      return Arrays.asList(WorkItemType.Task);
   }

   @Override
   protected boolean showWorkItemWidgets() {
      return false;
   }

}
