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

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.component.annotations.Component;

/**
 * Provides a Yes/No combo for Operational Impact. When "Yes" is selected a description text area appears. Persists to
 * {@link AtsAttributeTypes#OperationalImpact} and {@link AtsAttributeTypes#OperationalImpactDescription}.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXOperationalImpactWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetIdAts.XXOperationalImpactWidget;
   private static final String YES = "Yes";
   private static final String NO = "No";
   private static final int TEXT_HEIGHT_HINT_EMPTY = 30;

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   private CCombo impactCombo;
   private Label descLabel;
   private StyledText descText;
   private Composite detailComp;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXOperationalImpactWidget() {
      super(ID, "Operational Impact");
      set(XOption.VAL_NOT_APPLICABLE);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   // ------------------------------------------------------------------
   // UI construction
   // ------------------------------------------------------------------

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(4, false);
      layout.marginHeight = 1;
      comp.setLayout(layout);
      GridData compGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      compGd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(compGd);
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }

      createLabelWidget();

      impactCombo = new CCombo(comp, SWT.BORDER | SWT.READ_ONLY);
      impactCombo.setItems(YES_NO);
      GridData comboGd = new GridData(SWT.NONE, SWT.TOP, false, false);
      impactCombo.setLayoutData(comboGd);
      impactCombo.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            refreshDescription();
            notifyXModifiedListeners();
         }
      });

      // Placeholder composite for description label + text, occupying remaining columns
      detailComp = new Composite(comp, SWT.NONE);
      GridLayout detailLayout = new GridLayout(1, false);
      detailLayout.marginWidth = 0;
      detailLayout.marginHeight = 0;
      detailComp.setLayout(detailLayout);
      GridData detailGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      detailGd.horizontalSpan = 2;
      detailComp.setLayoutData(detailGd);
      if (getToolkit() != null) {
         getToolkit().adapt(detailComp);
      }

      // Load stored values if we already have an artifact
      if (xxWid.isTeamWf()) {
         revertFromArtifact();
      }

      refreshDescription();
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
      String impact = getImpact();
      return impact;
   }

   // ------------------------------------------------------------------
   // Persistence
   // ------------------------------------------------------------------

   /**
    * Saves the current widget values into the bound artifact's attributes.
    */
   public void saveToArtifact() {
      if (!xxWid.isTeamWf()) {
         return;
      }
      IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set Operational Impact");

      String impact = getImpact();
      if (impact == null || impact.isEmpty()) {
         changes.deleteSoleAttribute(teamWf, AtsAttributeTypes.OperationalImpact);
      } else {
         changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.OperationalImpact, impact);
      }
      String desc = getDescriptionStr();
      if (desc == null || desc.isEmpty()) {
         changes.deleteSoleAttribute(teamWf, AtsAttributeTypes.OperationalImpactDescription);
      } else {
         changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.OperationalImpactDescription, desc);
      }

      changes.executeIfNeeded();
   }

   /**
    * Reverts the widget to the values stored in the bound artifact.
    */
   public void revert() {
      revertFromArtifact();
   }

   /**
    * @return a {@link Result} with {@code isTrue()} when any value differs from the artifact
    */
   public Result isDirty() {
      if (!xxWid.isTeamWf()) {
         return Result.FalseResult;
      }
      IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
      String storedImpact = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
         AtsAttributeTypes.OperationalImpact, "");
      if (!getImpact().equals(storedImpact)) {
         return new Result(true, AtsAttributeTypes.OperationalImpact.toString());
      }
      String storedDesc = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
         AtsAttributeTypes.OperationalImpactDescription, "");
      if (!getDescriptionStr().equals(storedDesc)) {
         return new Result(true, AtsAttributeTypes.OperationalImpactDescription.toString());
      }
      return Result.FalseResult;
   }

   @Override
   protected TransactionToken handleSelectedPersist() {
      saveToArtifact();
      return TransactionToken.SENTINEL;
   }

   // ------------------------------------------------------------------
   // Base class overrides
   // ------------------------------------------------------------------

   @Override
   protected boolean handleSelection() {
      // Selection is handled directly by the embedded combo and text SWT controls
      return false;
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

   private static final String[] YES_NO = new String[] {YES, NO};

   /**
    * Shows or hides the description text area based on whether the combo is set to "Yes".
    */
   private void refreshDescription() {
      if (!Widgets.isAccessible(detailComp)) {
         return;
      }

      boolean showDesc = isImpactYes();

      if (showDesc) {
         if (descLabel == null || descLabel.isDisposed()) {
            descLabel = new Label(detailComp, SWT.NONE);
            descLabel.setText("Operational Impact Description:");
            descLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
         }
         if (descText == null || descText.isDisposed()) {
            descText = new StyledText(detailComp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            gd.heightHint = getTextHeightHint();
            gd.widthHint = 100;
            descText.setLayoutData(gd);
            descText.addModifyListener(new ModifyListener() {
               @Override
               public void modifyText(ModifyEvent e) {
                  notifyXModifiedListeners();
               }
            });
         }
      } else {
         disposeDescriptionControls();
      }

      detailComp.layout(true, true);
      comp.layout(true, true);
   }

   private void disposeDescriptionControls() {
      if (descLabel != null && !descLabel.isDisposed()) {
         descLabel.dispose();
         descLabel = null;
      }
      if (descText != null && !descText.isDisposed()) {
         descText.dispose();
         descText = null;
      }
   }

   private boolean isImpactYes() {
      return Widgets.isAccessible(impactCombo) && YES.equals(impactCombo.getText());
   }

   private int getTextHeightHint() {
      if (getDescriptionStr().isEmpty()) {
         return TEXT_HEIGHT_HINT_EMPTY;
      }
      return 0;
   }

   // ------------------------------------------------------------------
   // Value accessors
   // ------------------------------------------------------------------

   /** @return the current combo value ("Yes", "No", or empty string) */
   public String getImpact() {
      if (!Widgets.isAccessible(impactCombo)) {
         return "";
      }
      return impactCombo.getText();
   }

   /** @return the description text, or empty string if not visible */
   public String getDescriptionStr() {
      if (descText == null || !Widgets.isAccessible(descText)) {
         return "";
      }
      return descText.getText();
   }

   private void revertFromArtifact() {
      if (!xxWid.isTeamWf()) {
         return;
      }
      IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
      String impact = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
         AtsAttributeTypes.OperationalImpact, "");
      if (Widgets.isAccessible(impactCombo)) {
         impactCombo.setText(impact);
      }
      // Setting the combo triggers refreshDescription, so descText now exists if impact == "Yes"
      String desc = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
         AtsAttributeTypes.OperationalImpactDescription, "");
      if (descText != null && Widgets.isAccessible(descText)) {
         descText.setText(desc);
      }
   }

   @Override
   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (Widgets.isAccessible(impactCombo)) {
         impactCombo.setEnabled(enabled);
      }
      if (descText != null && Widgets.isAccessible(descText)) {
         descText.setEnabled(enabled);
      }
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   // ------------------------------------------------------------------
   // Artifact binding
   // ------------------------------------------------------------------

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
      if (Widgets.isAccessible(comp) && xxWid.isTeamWf()) {
         revertFromArtifact();
         refreshDescription();
      }
   }

}
