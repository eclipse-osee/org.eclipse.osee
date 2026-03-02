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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchQueryData;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.widget.ISelectableValueProvider;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWfdBranchSelWidget extends XAbstractHyperlinkWithFilteredDialogWidget<BranchToken> {

   public static final WidgetId ID = WidgetId.XHyperlinkWfdBranchSelWidget;

   ISelectableValueProvider valueProvider;

   public XHyperlinkWfdBranchSelWidget() {
      super(ID, "Branch");
   }

   @Override
   public Collection<BranchToken> getSelectable() {
      BranchQueryData bqd = widData.getBranchQuery();
      if (bqd == null) {
         throw new OseeArgumentException("BranchQueryData can not be null for [%s]", getClass().getSimpleName());
      }
      return Collections.castAll(ServiceUtil.getOseeClient().getBranchEndpoint().getBranches(bqd));
   }

}
