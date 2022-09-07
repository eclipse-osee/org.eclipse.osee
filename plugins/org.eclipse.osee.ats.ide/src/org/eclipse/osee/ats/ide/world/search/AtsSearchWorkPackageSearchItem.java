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

package org.eclipse.osee.ats.ide.world.search;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchWorkPackageSearchItem extends AbstractWorkItemSearchItem {

   private static final AtsImage IMAGE = AtsImage.WORK_PACKAGE;
   private static final String TITLE = "Work Package EV Search";
   public static final String NAMESPACE = AtsSearchUtil.ATS_QUERY_EV_NAMESPACE;

   public AtsSearchWorkPackageSearchItem() {
      super(TITLE, AtsSearchUtil.ATS_QUERY_EV_NAMESPACE, IMAGE);
   }

   public AtsSearchWorkPackageSearchItem(AbstractWorkItemSearchItem searchItem) {
      super(searchItem, TITLE, AtsSearchUtil.ATS_QUERY_EV_NAMESPACE, IMAGE);
   }

   public AtsSearchWorkPackageSearchItem(String title, String namespace, AtsImage image) {
      super(title, namespace, image);
   }

   @Override
   public AbstractWorkItemSearchItem copy() {
      AtsSearchWorkPackageSearchItem item = new AtsSearchWorkPackageSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AbstractWorkItemSearchItem copyProvider() {
      AtsSearchWorkPackageSearchItem item = new AtsSearchWorkPackageSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public String getShortNamePrefix() {
      return "EV";
   }

   @Override
   public Collection<WorkItemType> getWorkItemTypes() {
      return Arrays.asList(WorkItemType.TeamWorkflow, WorkItemType.Task, WorkItemType.Review);
   }

   @Override
   protected boolean showWorkPackageWidgets() {
      return true;
   }

}
