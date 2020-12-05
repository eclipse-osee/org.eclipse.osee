/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.metrics;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.world.AtsMetricsComposite;
import org.eclipse.osee.ats.ide.world.IAtsMetricsProvider;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class WfeMetricsTab extends WfeAbstractTab {
   private ScrolledForm scrolledForm;
   public final static String ID = "ats.metrics.tab";
   private final WorkflowEditor editor;
   private final IAtsMetricsProvider metricsProvider;

   public WfeMetricsTab(IAtsMetricsProvider metricsProvider, WorkflowEditor editor, IAtsWorkItem workItem) {
      super(editor, ID, workItem, "Metrics");
      this.metricsProvider = metricsProvider;
      this.editor = editor;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      try {

         bodyComp = scrolledForm.getBody();
         GridLayout gridLayout = new GridLayout(1, true);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, true);
         bodyComp.setLayoutData(gd);

         new AtsMetricsComposite(metricsProvider, bodyComp, SWT.NONE);

         updateTitleBar(managedForm);
         createToolbar(managedForm);
         FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);

      } catch (Exception ex) {
         handleException(ex);
      }
   }

}
