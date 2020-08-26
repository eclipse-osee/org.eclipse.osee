/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.event.IAtsWorkItemTopicEventListener;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class WfeStateCreatedOrigHeader extends Composite implements IWfeEventHandle, IAtsWorkItemTopicEventListener {

   private final IAtsWorkItem workItem;
   Label stateValueLabel, createdValueLabel;
   private final static Color BLOCKED_COLOR = new Color(null, 244, 80, 66);

   public WfeStateCreatedOrigHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(3, true));
      editor.getToolkit().adapt(this);

      try {
         stateValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Current State: ", "");
         createdValueLabel = FormsUtil.createLabelValue(editor.getToolkit(), this, "Created: ", "");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      final IAtsWorkItemTopicEventListener fListener = this;
      stateValueLabel.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            AtsApiService.get().getEventService().deRegisterAtsWorkItemTopicEvent(fListener);
         }
      });

      new WfeOriginatorHeader(this, SWT.NONE, workItem, editor);

      refresh();
      editor.registerEvent(this, AtsAttributeTypes.CurrentState, AtsAttributeTypes.CreatedDate);
      AtsApiService.get().getEventService().registerAtsWorkItemTopicEvent(this, AtsTopicEvent.WORK_ITEM_TRANSITIONED,
         AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED);
   }

   @Override
   public void handleEvent(AtsTopicEvent topicEvent, Collection<ArtifactId> workItems) {
      if (topicEvent.equals(AtsTopicEvent.WORK_ITEM_TRANSITIONED) || topicEvent.equals(
         AtsTopicEvent.WORK_ITEM_TRANSITION_FAILED)) {
         if (this.isDisposed()) {
            AtsApiService.get().getEventService().deRegisterAtsWorkItemTopicEvent(this);
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               refresh();
            }
         });
      }
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(stateValueLabel)) {
         String isBlocked = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem,
            AtsAttributeTypes.BlockedReason, "");
         if (Strings.isValid(isBlocked)) {
            stateValueLabel.setText(workItem.getStateMgr().getCurrentStateName() + " (Blocked)");
            stateValueLabel.setForeground(BLOCKED_COLOR);
         } else {
            stateValueLabel.setText(workItem.getStateMgr().getCurrentStateName());
         }
         createdValueLabel.setText(DateUtil.getMMDDYYHHMM(workItem.getCreatedDate()));
      }
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }
}
