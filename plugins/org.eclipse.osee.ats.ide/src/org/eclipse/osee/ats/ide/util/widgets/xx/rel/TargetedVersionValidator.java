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
package org.eclipse.osee.ats.ide.util.widgets.xx.rel;

import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XWidgetStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;

/**
 * Validator for XXTargetedVersionWidget that checks if a targeted version is selected when the work definition has the
 * RequireTargetedVersion option.
 *
 * @author Donald G. Dunne
 */
public class TargetedVersionValidator implements IValueValidator {

   private final XXWorkItemData xxWid;
   private final String label;

   public TargetedVersionValidator(XXWorkItemData xxWid, String label) {
      this.xxWid = xxWid;
      this.label = label;
   }

   @Override
   public XResultData isValid(Collection<? extends Object> objects) {
      if (!xxWid.isTeamWf()) {
         return XWidgetStatus.OK;
      }
      IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
      if (teamWf == null) {
         return XWidgetStatus.OK;
      }
      if (!teamWf.getWorkDefinition().hasOption(WorkDefOption.RequireTargetedVersion)) {
         return XWidgetStatus.OK;
      }
      if (objects == null || objects.isEmpty()) {
         return new XResultData(XResultType.Error, "Must Select %s", label);
      }
      return XWidgetStatus.OK;
   }

   @Override
   public XResultData isValid(Object obj) {
      return XWidgetStatus.OK;
   }

}
