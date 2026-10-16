/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A single SWT Composite that provides the full Operational Impact with Workaround workflow in a side-by-side layout:
 *
 * <pre>
 * +-------------------------------+-------------------------------+
 * | Operational Impact: [Yes/No]  | Workaround: [Yes/No]          |
 * | Impact Description:           | Workaround Description:       |
 * | +---------------------------+ | +---------------------------+ |
 * | |                           | | |                           | |
 * | +---------------------------+ | +---------------------------+ |
 * +-------------------------------+-------------------------------+
 * </pre>
 *
 * The left column (impact combo + description) appears when the impact combo is "Yes". The right column (workaround
 * combo + description) appears alongside the left column when the impact combo is "Yes", and the workaround description
 * text appears when the workaround combo is also "Yes".
 * <p>
 * This composite can optionally be bound to a {@link TeamWorkFlowArtifact} for persistence via
 * {@link #setArtifact(Artifact)}, {@link #saveToArtifact()}, {@link #revert()}, and {@link #isDirty()}.
 *
 * @author Donald G. Dunne
 */
public class XXOperationalImpactWithWorkaroundComposite extends Composite {

   private static final String YES = "Yes";
   private static final String NO = "No";
   private static final String[] YES_NO = new String[] {YES, NO};
   private static final int TEXT_HEIGHT_HINT = 60;
   private static final int TEXT_WIDTH_HINT = 150;

   private TeamWorkFlowArtifact teamArt;

   // Impact controls
   private CCombo impactCombo;
   private StyledText impactDescText;

   // Workaround controls
   private Label workaroundLabel;
   private CCombo workaroundCombo;
   private StyledText workaroundDescText;

   // Combo row at the top
   private Composite comboRow;
   // Left panel: impact description area (shown when impact == Yes)
   private Composite leftPanel;
   // Right panel: workaround description (shown when workaround == Yes)
   private Composite rightPanel;

   public XXOperationalImpactWithWorkaroundComposite(Composite parent, int style) {
      super(parent, style);
      createControls();
   }

   // ------------------------------------------------------------------
   // UI construction
   // ------------------------------------------------------------------

   private void createControls() {
      // Top-level: single column so the combo row and the detail row stack vertically
      setLayout(new GridLayout(1, false));
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      // --- Combo row: both combos on the same horizontal line ---
      comboRow = new Composite(this, SWT.NONE);
      comboRow.setLayout(new GridLayout(4, false));
      comboRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      Label impactLabel = new Label(comboRow, SWT.NONE);
      impactLabel.setText("Operational Impact:");

      impactCombo = new CCombo(comboRow, SWT.BORDER | SWT.READ_ONLY);
      impactCombo.setItems(YES_NO);
      impactCombo.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            refreshPanels();
         }
      });

      // Workaround label + combo are created in the same row but hidden until needed
      workaroundLabel = new Label(comboRow, SWT.NONE);
      workaroundLabel.setText("Workaround:");
      workaroundLabel.setVisible(false);
      excludeFromLayout(workaroundLabel, true);

      workaroundCombo = new CCombo(comboRow, SWT.BORDER | SWT.READ_ONLY);
      workaroundCombo.setItems(YES_NO);
      workaroundCombo.setVisible(false);
      excludeFromLayout(workaroundCombo, true);
      workaroundCombo.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            refreshDescriptionPanels();
         }
      });

      // --- Detail row: two equal-width panels side by side for the description text areas ---
      Composite detailRow = new Composite(this, SWT.NONE);
      GridLayout detailLayout = new GridLayout(2, true);
      detailLayout.marginWidth = 0;
      detailLayout.marginHeight = 0;
      detailRow.setLayout(detailLayout);
      detailRow.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      leftPanel = new Composite(detailRow, SWT.NONE);
      leftPanel.setLayout(new GridLayout(1, false));
      leftPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      rightPanel = new Composite(detailRow, SWT.NONE);
      rightPanel.setLayout(new GridLayout(1, false));
      rightPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   /** Shows or hides a control and excludes/includes it from GridLayout calculations. */
   private static void excludeFromLayout(Control control, boolean exclude) {
      GridData gd = (GridData) control.getLayoutData();
      if (gd == null) {
         gd = new GridData();
         control.setLayoutData(gd);
      }
      gd.exclude = exclude;
   }

   /**
    * Rebuilds both description panels and shows/hides the workaround combo based on the impact combo selection.
    */
   private void refreshPanels() {
      disposeChildren(leftPanel);
      disposeChildren(rightPanel);
      impactDescText = null;
      workaroundDescText = null;

      boolean showDetails = isImpactSelected();

      // Show or hide the workaround combo in the combo row
      workaroundLabel.setVisible(showDetails);
      excludeFromLayout(workaroundLabel, !showDetails);
      workaroundCombo.setVisible(showDetails);
      excludeFromLayout(workaroundCombo, !showDetails);
      if (!showDetails) {
         workaroundCombo.setText("");
      }
      comboRow.layout(true, true);

      if (showDetails) {
         // --- Left panel: impact description ---
         Label descLabel = new Label(leftPanel, SWT.NONE);
         descLabel.setText("Operational Impact Description:");
         descLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

         impactDescText = new StyledText(leftPanel, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
         GridData descGd = new GridData(SWT.FILL, SWT.FILL, true, true);
         descGd.heightHint = TEXT_HEIGHT_HINT;
         descGd.widthHint = TEXT_WIDTH_HINT;
         impactDescText.setLayoutData(descGd);

         // --- Right panel: workaround description (if workaround == Yes) ---
         refreshDescriptionPanels();
      }

      leftPanel.layout(true, true);
      rightPanel.layout(true, true);
      layout(true, true);
   }

   /**
    * Shows or hides the workaround description text in the right panel based on the workaround combo selection.
    */
   private void refreshDescriptionPanels() {
      disposeChildren(rightPanel);
      workaroundDescText = null;

      if (isWorkaroundSelected()) {
         Label waDescLabel = new Label(rightPanel, SWT.NONE);
         waDescLabel.setText("Workaround Description:");
         waDescLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

         workaroundDescText = new StyledText(rightPanel, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.heightHint = TEXT_HEIGHT_HINT;
         gd.widthHint = TEXT_WIDTH_HINT;
         workaroundDescText.setLayoutData(gd);
      }

      rightPanel.layout(true, true);
      layout(true, true);
   }

   private static void disposeChildren(Composite composite) {
      for (Control child : composite.getChildren()) {
         child.dispose();
      }
   }

   // ------------------------------------------------------------------
   // Selection helpers
   // ------------------------------------------------------------------

   private boolean isImpactSelected() {
      return Widgets.isAccessible(impactCombo) && YES.equals(impactCombo.getText());
   }

   private boolean isWorkaroundSelected() {
      return Widgets.isAccessible(workaroundCombo) && YES.equals(workaroundCombo.getText());
   }

   // ------------------------------------------------------------------
   // Value accessors
   // ------------------------------------------------------------------

   /** @return the current Operational Impact combo value, or empty string if not set */
   public String getImpact() {
      if (!Widgets.isAccessible(impactCombo)) {
         return "";
      }
      return impactCombo.getText();
   }

   /** Sets the Operational Impact combo value */
   public void setImpact(String value) {
      if (Widgets.isAccessible(impactCombo)) {
         impactCombo.setText(value == null ? "" : value);
      }
   }

   /** @return the current impact description text, or empty string if not visible */
   public String getImpactDescription() {
      if (impactDescText == null || !Widgets.isAccessible(impactDescText)) {
         return "";
      }
      return impactDescText.getText();
   }

   /** Sets the impact description text (only effective when the description area is visible) */
   public void setImpactDescription(String value) {
      if (impactDescText != null && Widgets.isAccessible(impactDescText)) {
         impactDescText.setText(value == null ? "" : value);
      }
   }

   /** @return the current Workaround combo value, or empty string if not visible */
   public String getWorkaroundImpact() {
      if (workaroundCombo == null || !Widgets.isAccessible(workaroundCombo)) {
         return "";
      }
      return workaroundCombo.getText();
   }

   /** Sets the Workaround combo value */
   public void setWorkaroundImpact(String value) {
      if (workaroundCombo != null && Widgets.isAccessible(workaroundCombo)) {
         workaroundCombo.setText(value == null ? "" : value);
      }
   }

   /** @return the current workaround description text, or empty string if not visible */
   public String getWorkaroundDescription() {
      if (workaroundDescText == null || !Widgets.isAccessible(workaroundDescText)) {
         return "";
      }
      return workaroundDescText.getText();
   }

   /** Sets the workaround description text */
   public void setWorkaroundDescription(String value) {
      if (workaroundDescText != null && Widgets.isAccessible(workaroundDescText)) {
         workaroundDescText.setText(value == null ? "" : value);
      }
   }

   // ------------------------------------------------------------------
   // Artifact binding
   // ------------------------------------------------------------------

   /**
    * Binds this composite to the given artifact for persistence. Only accepts {@link TeamWorkFlowArtifact} instances.
    */
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.teamArt = (TeamWorkFlowArtifact) artifact;
      }
   }

   /** @return the bound artifact, or {@code null} if none is set */
   public Artifact getArtifact() {
      return teamArt;
   }

   /**
    * Persists the current widget values into the bound {@link TeamWorkFlowArtifact}'s attributes. Empty or null values
    * cause the corresponding attribute to be deleted.
    */
   public void saveToArtifact() {
      if (teamArt == null) {
         return;
      }
      saveAttribute(getImpact(), AtsAttributeTypes.OperationalImpact);
      saveAttribute(getImpactDescription(), AtsAttributeTypes.OperationalImpactDescription);
      saveAttribute(getWorkaroundImpact(), AtsAttributeTypes.OperationalImpactWorkaround);
      saveAttribute(getWorkaroundDescription(), AtsAttributeTypes.OperationalImpactWorkaroundDescription);
   }

   private void saveAttribute(String value, org.eclipse.osee.framework.core.data.AttributeTypeId attrType) {
      if (value == null || value.isEmpty()) {
         teamArt.deleteSoleAttribute(attrType);
      } else {
         teamArt.setSoleAttributeValue(attrType, value);
      }
   }

   /**
    * Reverts the widget values back to what is stored in the bound artifact.
    */
   public void revert() {
      if (teamArt == null) {
         return;
      }
      try {
         String impact = teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, "");
         setImpact(impact);
         // Setting the combo triggers refreshPanels, so the description/workaround controls now exist if needed
         setImpactDescription(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, ""));
         setWorkaroundImpact(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, ""));
         setWorkaroundDescription(
            teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaroundDescription, ""));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   /**
    * Compares the current widget values against the bound artifact's stored attributes.
    *
    * @return a {@link Result} with {@code isTrue()} when any value differs from the artifact
    */
   public Result isDirty() {
      if (teamArt == null) {
         return Result.FalseResult;
      }
      if (!getImpact().equals(teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpact, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpact.toString());
      }
      if (!getImpactDescription().equals(
         teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactDescription, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactDescription.toString());
      }
      if (!getWorkaroundImpact().equals(
         teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactWorkaround.toString());
      }
      if (!getWorkaroundDescription().equals(
         teamArt.getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaroundDescription, ""))) {
         return new Result(true, AtsAttributeTypes.OperationalImpactWorkaroundDescription.toString());
      }
      return Result.FalseResult;
   }

   // ------------------------------------------------------------------
   // Enable / disable
   // ------------------------------------------------------------------

   @Override
   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (Widgets.isAccessible(impactCombo)) {
         impactCombo.setEnabled(enabled);
      }
      if (impactDescText != null && Widgets.isAccessible(impactDescText)) {
         impactDescText.setEnabled(enabled);
      }
      if (workaroundCombo != null && Widgets.isAccessible(workaroundCombo)) {
         workaroundCombo.setEnabled(enabled);
      }
      if (workaroundDescText != null && Widgets.isAccessible(workaroundDescText)) {
         workaroundDescText.setEnabled(enabled);
      }
   }

   // ------------------------------------------------------------------
   // Standalone test harness
   // ------------------------------------------------------------------

   public static void main(String[] args) {
      Display display = Display.getDefault();
      Shell shell = new Shell(display, SWT.SHELL_TRIM);
      shell.setText("Operational Impact With Workaround");
      shell.setBounds(100, 100, 700, 300);
      shell.setLayout(new GridLayout(1, false));

      XXOperationalImpactWithWorkaroundComposite widget =
         new XXOperationalImpactWithWorkaroundComposite(shell, SWT.NONE);
      widget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      shell.open();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
      display.dispose();
   }
}
