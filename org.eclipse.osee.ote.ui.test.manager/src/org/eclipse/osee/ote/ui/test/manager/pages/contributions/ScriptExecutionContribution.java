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
package org.eclipse.osee.ote.ui.test.manager.pages.contributions;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.ui.test.manager.panels.FileOrDirectorySelectionPanel;
import org.eclipse.osee.ote.ui.test.manager.panels.LoggingPanel;
import org.eclipse.osee.ote.ui.test.manager.panels.ScriptExecutionOptionsPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * @author Roberto E. Escobar
 */
public class ScriptExecutionContribution implements IAdvancedPageContribution {
   private static final String GROUP_TITLE = "Script Execution Options";
   private static final String LOGGING_GROUP = "Select a logging level";
   private static final String EXECUTION_OPTIONS_GROUP = "Execution Options";
   private static final String SCRIPT_OUTPUT_TOOLTIP =
         "If the path in the text box is an existing directory all files will be written there.\n" + "If the path is empty the files will be written to the same location as the script.\n" + "In all other cases the path will be relative to the parent project of the file.\n";
   private static final String SCRIPT_OUTPUT_LABEL = "Script Output Directory: ";

   private FileOrDirectorySelectionPanel scriptDirectoryPanel;
   private LoggingPanel loggingPanel;
   private ScriptExecutionOptionsPanel optionsPanel;

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#createControl(org.eclipse.swt.widgets.Composite)
    */
   public Control createControl(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText(GROUP_TITLE);

      this.scriptDirectoryPanel =
            new FileOrDirectorySelectionPanel(group, SWT.NONE, SCRIPT_OUTPUT_LABEL, SCRIPT_OUTPUT_TOOLTIP, true);

      Composite composite = new Composite(group, SWT.NONE);
      GridLayout gl = new GridLayout(2, false);
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      composite.setLayout(gl);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      Group scriptOptionsGroup = new Group(composite, SWT.NONE);
      scriptOptionsGroup.setLayout(new GridLayout());
      scriptOptionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      scriptOptionsGroup.setText(EXECUTION_OPTIONS_GROUP);

      this.optionsPanel = new ScriptExecutionOptionsPanel(scriptOptionsGroup, SWT.NONE);

      Group loggingGroup = new Group(composite, SWT.NONE);
      loggingGroup.setLayout(new GridLayout());
      loggingGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      loggingGroup.setText(LOGGING_GROUP);

      this.loggingPanel = new LoggingPanel(loggingGroup, SWT.NONE);
      return group;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#load(org.eclipse.osee.ote.core.framework.data.IPropertyStore)
    */
   public void load(IPropertyStore propertyStore) {
      this.scriptDirectoryPanel.setSelected(propertyStore.get(TestManagerStorageKeys.SCRIPT_OUTPUT_DIRECTORY_KEY));

      this.optionsPanel.setKeepOldCopiesEnabled(propertyStore.getBoolean(TestManagerStorageKeys.KEEP_OLD_OUTFILE_COPIES_ENABLED_KEY));
      this.optionsPanel.setBatchModeEnabled(propertyStore.getBoolean(TestManagerStorageKeys.BATCH_MODE_ENABLED_KEY));

      this.loggingPanel.setSelected(propertyStore.get(TestManagerStorageKeys.LOGGING_LEVEL_KEY));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#save(org.eclipse.osee.ote.core.framework.data.IPropertyStore)
    */
   public void save(IPropertyStore propertyStore) {
      propertyStore.put(TestManagerStorageKeys.SCRIPT_OUTPUT_DIRECTORY_KEY, this.scriptDirectoryPanel.getSelected());

      propertyStore.put(TestManagerStorageKeys.LOGGING_LEVEL_KEY, this.loggingPanel.getSelected());

      propertyStore.put(TestManagerStorageKeys.KEEP_OLD_OUTFILE_COPIES_ENABLED_KEY,
            this.optionsPanel.isKeepOldCopiesEnabled());
      propertyStore.put(TestManagerStorageKeys.BATCH_MODE_ENABLED_KEY, this.optionsPanel.isBatchModeEnabled());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#areSettingsValidForRun()
    */
   @Override
   public boolean areSettingsValidForRun() {
      return scriptDirectoryPanel.isValid();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#getErrorMessage()
    */
   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      if (scriptDirectoryPanel.isValid() != true) {
         builder.append("Script Output Directory: ");
         builder.append(scriptDirectoryPanel.getErrorMessage());
      }
      return builder.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.contributions.IAdvancedPageContribution#getPriority()
    */
   public int getPriority() {
      return 0;
   }
}
