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

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXOperImpactWithWorkArndWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetIdAts.XXOperImpactWithWorkArndWidget;

   private XXOperationalImpactWithWorkaroundComposite impactComposite;
   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXOperImpactWithWorkArndWidget() {
      super(ID, "Operational Impact");
      set(XOption.VAL_NOT_APPLICABLE);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 1;
      comp.setLayout(layout);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(gd);
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }

      createLabelWidget();

      impactComposite = new XXOperationalImpactWithWorkaroundComposite(comp, SWT.NONE);
      impactComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (xxWid.isTeamWf()) {
         impactComposite.setArtifact((Artifact) xxWid.getTeamWf().getStoreObject());
         impactComposite.revert();
      }

      refresh();
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   @Override
   public String getDisplayValue() {
      if (impactComposite == null || impactComposite.isDisposed()) {
         return "";
      }
      String impact = impactComposite.getImpact();
      if (impact == null || impact.isEmpty()) {
         return "";
      }
      return impact;
   }

   @Override
   protected boolean handleSelection() {
      // Selection is handled directly by the embedded composite's SWT controls
      return false;
   }

   @Override
   protected TransactionToken handleSelectedPersist() {
      if (impactComposite != null && !impactComposite.isDisposed()) {
         impactComposite.saveToArtifact();
      }
      return TransactionToken.SENTINEL;
   }

   public void saveToArtifact() {
      if (impactComposite != null && !impactComposite.isDisposed()) {
         impactComposite.saveToArtifact();
      }
   }

   public void revert() {
      if (impactComposite != null && !impactComposite.isDisposed()) {
         impactComposite.revert();
      }
   }

   public Result isDirty() {
      if (impactComposite != null && !impactComposite.isDisposed()) {
         return impactComposite.isDirty();
      }
      return Result.FalseResult;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public void refresh() {
      if (!Widgets.isAccessible(comp)) {
         return;
      }
      validate();
   }

   @Override
   public Control getControl() {
      return comp;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public XXOperationalImpactWithWorkaroundComposite getImpactComposite() {
      return impactComposite;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
      if (impactComposite != null && !impactComposite.isDisposed()) {
         if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            impactComposite.setArtifact(artifact);
            impactComposite.revert();
         }
      }
   }

}
