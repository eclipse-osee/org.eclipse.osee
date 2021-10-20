/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.skynet.widgets.ISelectableValueProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWithFilteredDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForActiveAis extends XHyperlinkWithFilteredDialog<IAtsActionableItem> {

   ISelectableValueProvider valueProvider;

   public XHyperlinkWfdForActiveAis() {
      super("Actionable Item(s)");
   }

   @Override
   public Collection<IAtsActionableItem> getSelectable() {
      return AtsApiService.get().getActionableItemService().getTopLevelActionableItems(Active.Active);
   }

}
