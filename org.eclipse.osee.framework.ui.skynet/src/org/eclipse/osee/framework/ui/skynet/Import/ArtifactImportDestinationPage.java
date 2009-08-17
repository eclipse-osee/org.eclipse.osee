/*
 * Created on Aug 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTypeFilteredTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan C. Schmitt
 * @author Roberto E. Escobar
 */
public class ArtifactImportDestinationPage extends WizardDataTransferPage {

   private static final String PAGE_NAME = "osee.define.wizardPage.artifactImportDestinationPage";
   private List attributeTypeList;

   protected ArtifactImportDestinationPage() {
      super(PAGE_NAME);
      setTitle("Import Artifacts into OSEE");
      setDescription("Select destination for imported artifacts.");
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.dialogs.WizardDataTransferPage#allowNewContainerName()
    */
   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
    */
   @Override
   public void handleEvent(Event arg0) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      createImportAsArtifactTypeArea(composite);
      createDestinationOptionsArea(composite);

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   private void createImportAsArtifactTypeArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      composite.setText("Import data as artifact type");

      attributeTypeList = new List(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      attributeTypeList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Button selectTypes = new Button(composite, SWT.PUSH);
      selectTypes.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleAttributeTypeSelection();
         }
      });
   }

   private void createDestinationOptionsArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      composite.setText("Where to Import Artifacts");

   }

   private void handleAttributeTypeSelection() {
      Collection<ArtifactType> artifactTypes = null;
      try {
         artifactTypes = ArtifactTypeManager.getAllTypes();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         artifactTypes = Collections.emptyList();
      }
      String title = "Import as Artifact Type";
      String message = "Select what artifact type data should be imported as.";
      ArtifactTypeFilteredTreeDialog dialog = new ArtifactTypeFilteredTreeDialog(title, message, artifactTypes);
      Object lastSelected = attributeTypeList.getData(attributeTypeList.getItem(0));
      if (lastSelected != null) {
         try {
            dialog.setInitialSelections(ArtifactTypeManager.getType(lastSelected.toString()));
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
                  "Dialog could not be initialized to the last Artifact Type selected", ex);
         }
      }
      int result = dialog.open();
      if (result == Window.OK) {
         ArtifactType artifactType = dialog.getSelection();
         String key = artifactType.getName();
         attributeTypeList.add(key);
         attributeTypeList.setData(key, artifactType);
      }
   }
}
