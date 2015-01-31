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
package org.eclipse.osee.ote.ui.test.manager.panels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Roberto E. Escobar
 */
public class ScriptExecutionOptionsPanel extends Composite {
   private Button saveOutputCheck;
   private Button batchModeCheck;
   private Button abortScriptOnFirstFail;
   private Button pauseScriptOnFail;
   private Button printFailToConsole;

   public ScriptExecutionOptionsPanel(Composite parent, int style) {
      super(parent, style);
      GridLayout gl = new GridLayout();
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      this.setLayout(gl);
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      createControl(this);
   }

   private void createControl(Composite parent) {
      saveOutputCheck = new Button(parent, SWT.CHECK);
      saveOutputCheck.setText("Keep copies of old Output Files");
      saveOutputCheck.setToolTipText("Select to save existing output filename\n" + "to file \"<output>.[num].tmo\" for each succssive run.\n" + "De-select overwrites output file.\n\n" + "     eg. myScript.tmo = current output\n" + "         myScript.1.tmo = oldest output file\n" + "         myScript.2.tmo = output before current");

      batchModeCheck = new Button(parent, SWT.CHECK);
      batchModeCheck.setText("Run in batch mode");
      batchModeCheck.setToolTipText("If any prompts exist, they are skipped if this option is selected.");
      
      abortScriptOnFirstFail = new Button(parent, SWT.CHECK);
      abortScriptOnFirstFail.setText("Abort script on first fail");
      abortScriptOnFirstFail.setToolTipText("Any script fail will cause the current script to be aborted.");
      
      pauseScriptOnFail = new Button(parent, SWT.CHECK);
      pauseScriptOnFail.setText("Pause script on fail");
      pauseScriptOnFail.setToolTipText("Each script failure will cause a promptPause to occur.");

      printFailToConsole = new Button(parent, SWT.CHECK);
      printFailToConsole.setText("Print failures to console");
      printFailToConsole.setToolTipText("Prints each failure to the console as the script runs.");
   }

   public boolean isKeepOldCopiesEnabled() {
      return saveOutputCheck.getSelection();
   }

   public void setKeepOldCopiesEnabled(boolean isEnabled) {
      saveOutputCheck.setSelection(isEnabled);
   }

   public boolean isBatchModeEnabled() {
      return batchModeCheck.getSelection();
   }

   public void setBatchModeEnabled(boolean isEnabled) {
      batchModeCheck.setSelection(isEnabled);
   }
   
   public boolean isAbortOnFail(){
      return abortScriptOnFirstFail.getSelection();
   }
   
   public void setAbortOnFail(boolean isEnabled){
      abortScriptOnFirstFail.setSelection(isEnabled);
   }
   
   public boolean isPauseOnFail(){
      return pauseScriptOnFail.getSelection();
   }
   
   public void setPauseOnFail(boolean isEnabled){
      pauseScriptOnFail.setSelection(isEnabled);
   }

   public boolean isPrintFailToConsole(){
      return printFailToConsole.getSelection();
   }
   
   public void setPrintFailToConsole(boolean isEnabled){
      printFailToConsole.setSelection(isEnabled);
   }
}
