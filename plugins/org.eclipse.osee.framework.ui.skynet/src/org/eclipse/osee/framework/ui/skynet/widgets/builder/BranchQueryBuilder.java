/*******************************************************************************
 * Copyright (c) 2026 Boeing.
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
package org.eclipse.osee.framework.ui.skynet.widgets.builder;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchQueryData;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.widget.XWidgetData;

/**
 * @author Donald G. Dunne
 */
public class BranchQueryBuilder {

   XWidgetData widData;
   BranchQueryData queryData;
   private final XWidgetBuilder wb;

   public BranchQueryBuilder(XWidgetBuilder wb) {
      this(null, wb);
   }

   public BranchQueryBuilder(XWidgetData widData, XWidgetBuilder wb) {
      this.widData = widData;
      this.wb = wb;
      this.queryData = new BranchQueryData();
      if (widData != null) {
         this.widData.setBranchQuery(queryData);
      }
   }

   public BranchQueryBuilder andBranchType(BranchType... branchTypes) {
      for (BranchType type : branchTypes) {
         queryData.getBranchTypes().add(type);
      }
      return this;
   }

   public XWidgetBuilder endQuery() {
      return wb;
   }

   public BranchQueryData getBranchQueryData() {
      return queryData;
   }

   public BranchQueryBuilder andBranchState(BranchState... branchStates) {
      for (BranchState state : branchStates) {
         queryData.getBranchStates().add(state);
      }
      return this;
   }

   public BranchQueryBuilder andBranchId(Long id) {
      return andBranchId(BranchId.valueOf(id));
   }

   public BranchQueryBuilder andBranchId(BranchId branchId) {
      queryData.getBranchIds().add(branchId);
      return this;
   }

}
