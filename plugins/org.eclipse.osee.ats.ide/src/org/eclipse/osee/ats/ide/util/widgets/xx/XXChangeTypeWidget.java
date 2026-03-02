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
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsSelWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXChangeTypeWidget extends XXStringsSelWidget {

   public static final WidgetId ID = WidgetIdAts.XXChangeTypeWidget;
   public static List<String> DefaultChangeTypes =
      Arrays.asList(ChangeTypes.Improvement.toString(), ChangeTypes.Problem.name(), ChangeTypes.Support.name());
   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   public XXChangeTypeWidget() {
      super(ID, "Change Type");
   }

   @Override
   public void setWidData(XWidgetData widData) {
      super.setWidData(widData);
      setAttributeType(AtsAttributeTypes.ChangeType);
   }

   @Override
   public Collection<String> getSelectable() {
      if (xxWid.isWorkItem()) {
         IAtsWorkItem workItem = xxWid.getWorkItem();
         List<ChangeTypes> changeTypes = AtsApiService.get().getWorkItemService().getChangeTypeOptions(workItem);
         return Collections.toStrings(changeTypes);
      }
      return DefaultChangeTypes;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
