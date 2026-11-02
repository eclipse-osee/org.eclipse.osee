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
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.widget.ISelectableValueProvider;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXBranchViewWidget extends XXAbstractBaseXWidget<BranchViewToken> {

   public static final WidgetId ID = WidgetId.XXBranchViewWidget;

   ISelectableValueProvider valueProvider;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXBranchViewWidget() {
      super(ID, "Branch View");
      set(XOption.VAL_SINGLE_SEL);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<BranchViewToken> getSelectable() {
      List<BranchViewToken> branchViews = new ArrayList<BranchViewToken>();
      Collection<BranchViewToken> selectable = super.getSelectable();
      if (Collections.isNotEmpty(selectable)) {
         branchViews.addAll(selectable);
      } else {
         Collection<Object> values = widData.getValues();
         if (values != null) {
            for (Object obj : values) {
               if (obj instanceof BranchViewToken) {
                  branchViews.add((BranchViewToken) obj);
               }
            }
         }
      }
      return branchViews;
   }

   @Override
   public BranchViewToken getSentinel() {
      return BranchViewToken.SENTINEL;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

}
