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

import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.search.AtsSearchWorkflowSearchItem;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkItemSearchItem extends AtsSearchWorkflowSearchItem {

   private final String namespace;

   public AbstractWorkItemSearchItem(String title, String namespace, AtsImage image) {
      super(title, image);
      this.namespace = namespace;
   }

   public AbstractWorkItemSearchItem(AbstractWorkItemSearchItem searchItem, String title, String namespace, AtsImage image) {
      super(searchItem, title, image);
      this.namespace = namespace;
   }

   @Override
   public String getNamespace() {
      return namespace;
   }

   @Override
   public AtsSearchData loadSearchData(AtsSearchData data) {
      super.loadSearchData(data);
      data.getWorkItemTypes().clear();
      data.getWorkItemTypes().addAll(getWorkItemTypes());
      return data;
   }

   abstract Collection<WorkItemType> getWorkItemTypes();

}
