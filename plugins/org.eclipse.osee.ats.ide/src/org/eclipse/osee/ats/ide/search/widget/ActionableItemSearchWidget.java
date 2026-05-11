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

package org.eclipse.osee.ats.ide.search.widget;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemSearchWidget extends AbstractSearchWidget<XHyperlabelActionableItemSelection, IAtsActionableItem> {

   public static SearchWidget ActionableItemWidget =
      new SearchWidget(123423452, "Actionable Item(s)", "XHyperlabelActionableItemSelection");

   public ActionableItemSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(ActionableItemWidget, searchItem);
   }

   public Collection<Long> getIds() {
      List<Long> ids = new LinkedList<>();
      if (get() != null) {
         for (IAtsActionableItem ai : get()) {
            ids.add(ai.getId());
         }
      }
      return ids;
   }

   public Collection<IAtsActionableItem> get() {
      XHyperlabelActionableItemSelection widget = getWidget();
      if (widget != null) {
         return widget.getSelectedActionableItems();
      }
      return null;
   }

   public void set(Collection<IAtsActionableItem> ais) {
      if (getWidget() != null) {
         getWidget().setSelectedAIs(ais);
      }
   }

   @Override
   public void set(AtsSearchData data) {
      List<IAtsActionableItem> ais = new LinkedList<>();
      for (Long id : data.getAiIds()) {
         IAtsActionableItem ai =
            AtsApiService.get().getActionableItemService().getActionableItemById(ArtifactId.valueOf(id));
         if (ai != null) {
            ais.add(ai);
         }
      }
      set(ais);
   }

   @Override
   public void widgetCreated(XWidget xWidget) {
      super.widgetCreated(xWidget);
      getWidget().addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            searchItem.updateAisOrTeamDefs();
         }
      });
   }

}
