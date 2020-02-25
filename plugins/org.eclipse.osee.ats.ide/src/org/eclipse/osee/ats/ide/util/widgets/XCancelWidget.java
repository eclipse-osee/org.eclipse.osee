/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Arrays;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Jeremy A. Midvidy
 */
public class XCancelWidget extends GenericXWidget implements IArtifactWidget {

   public static final String WIDGET_ID = XCancelWidget.class.getSimpleName();
   public static final String DISPLAY_LABEL = "CancelBox";
   private final XComboDam cancelledReason;
   private final XTextDam cancelledDetails;
   private Composite composite;
   private Artifact art;

   public XCancelWidget() {
      this.cancelledReason = new XComboDam("Cancelled Reason");
      this.cancelledDetails = new XTextDam("Cancelled Details");
      this.art = null;
   }

   public XCancelWidget(Artifact sma) {
      this.cancelledReason = new XComboDam("Cancelled Reason");
      this.cancelledDetails = new XTextDam("Cancelled Details");
      this.art = sma;
   }

   private String[] getCancelledReasonValues() {
      Set<String> valuesSet = AttributeTypeManager.getEnumerationValues(AtsAttributeTypes.CancelReason.getName());
      String[] values = valuesSet.toArray(new String[valuesSet.size()]);
      Arrays.sort(values);
      return values;
   }

   public void updateRequiredTextEntry(boolean isRequired) {
      this.cancelledDetails.setRequiredEntry(isRequired);
   }

   @Override
   public Control getControl() {
      return this.composite;
   }

   public String getComboSelection() {
      return this.cancelledReason.get();
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      Composite comp = new Composite(parent, SWT.LEFT);
      comp.setLayout(ALayout.getZeroMarginLayout(1, true));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(gd);
      this.cancelledReason.setAttributeType(getArtifact(), AtsAttributeTypes.CancelReason);
      this.cancelledDetails.setAttributeType(getArtifact(), AtsAttributeTypes.CancelledReasonDetails);
      this.cancelledDetails.setFillVertically(true);
      this.cancelledReason.setDataStrings(getCancelledReasonValues());
      this.cancelledReason.createWidgets(comp, horizontalSpan);
      this.cancelledDetails.createWidgets(comp, horizontalSpan);
      this.cancelledReason.addXModifiedListener(new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            if (widget.getData().toString().equals("Other (Must enter cancelled details)")) {
               updateRequiredTextEntry(true);
            } else {
               updateRequiredTextEntry(false);
            }
         }
      });
      this.composite = comp;
   }

   @Override
   public Artifact getArtifact() {
      return this.art;
   }

   @Override
   public void saveToArtifact() {
      if (this.cancelledReason.get().contains("Other") && this.cancelledDetails.get().equals("")) {
         AWorkbench.popup("Error! Cannot save.",
            "If cancelled reason is \"Other\", you must specify details.  Please enter cancelled details.");
         return;
      }
      this.cancelledDetails.saveToArtifact();
      this.cancelledReason.saveToArtifact();
   }

   @Override
   public void revert() {
      cancelledDetails.revert();
      cancelledReason.revert();
   }

   @Override
   public Result isDirty() {
      Result result = cancelledDetails.isDirty();
      if (result.isTrue()) {
         return result;
      }
      result = cancelledReason.isDirty();
      if (result.isTrue()) {
         return result;
      }
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      this.art = artifact;
   }

}
