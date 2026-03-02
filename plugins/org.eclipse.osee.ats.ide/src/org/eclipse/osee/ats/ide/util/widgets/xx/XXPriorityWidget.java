/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.OseeEnum;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsSelWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXPriorityWidget extends XXStringsSelWidget {

   public static final WidgetId ID = WidgetIdAts.XXPriorityWidget;
   public static List<String> DefaultPriorities = Arrays.asList("1", "2", "3", "4", "5");
   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   public XXPriorityWidget() {
      super(ID, "Priority");
   }

   @Override
   public void setWidData(XWidgetData widData) {
      super.setWidData(widData);
      setAttributeType(AtsAttributeTypes.Priority);
      setSingleSelect(true);
   }

   @Override
   public Collection<String> getSelectable() {
      if (xxWid.isWorkItem()) {
         IAtsWorkItem workItem = xxWid.getWorkItem();
         List<Priorities> priorities = AtsApiService.get().getWorkItemService().getPrioritiesOptions(workItem);
         return OseeEnum.toStrings(priorities);
      }
      return DefaultPriorities;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
