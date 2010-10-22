/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.ats.artifact.log.LogType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkWidgetDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsCancellationReasonStateWorkItem extends WorkWidgetDefinition {

   public final static String ID = "ats.CancellationReason";

   protected AtsCancellationReasonStateWorkItem(AbstractWorkflowArtifact sma) throws OseeCoreException {
      super("Cancellation Reason", ID);
      DynamicXWidgetLayoutData data = new DynamicXWidgetLayoutData(null);
      data.setName(getName());
      LogItem item = sma.getLog().getStateEvent(LogType.StateCancelled);
      data.setDefaultValue(item.getMsg());
      data.setId(getId());
      data.setXWidgetName("XText");
      data.getXOptionHandler().add(XOption.NOT_EDITABLE);
      data.getXOptionHandler().add(XOption.FILL_HORIZONTALLY);
      setXWidgetLayoutData(data);
   }
}
