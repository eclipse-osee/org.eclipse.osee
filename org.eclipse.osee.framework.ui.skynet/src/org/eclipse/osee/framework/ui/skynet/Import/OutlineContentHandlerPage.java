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
package org.eclipse.osee.framework.ui.skynet.Import;

import org.eclipse.osee.framework.skynet.core.importing.ArtifactSourceParserContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.IArtifactSourceParser;
import org.eclipse.osee.framework.skynet.core.importing.IArtifactSourceParserDelegate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Robert A. Fisher
 */
public class OutlineContentHandlerPage extends WizardDataTransferPage implements Listener {
   public static final String PAGE_NAME = "osee.define.wizardPage.outlineContentHandlerPage";

   private final ArtifactSourceParserContributionManager contributionManager;
   private List handlerList;
   private boolean hasHandlers;

   /**
    * @param descriptors Available descriptors to select from
    */
   public OutlineContentHandlerPage() {
      super(PAGE_NAME);
      contributionManager = new ArtifactSourceParserContributionManager();
      hasHandlers = false;
   }

   @Override
   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
      composite.setFont(parent.getFont());

      createOptionsGroup(composite);

      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());

      setControl(composite);
   }

   /**
    * The <code>WizardResourceImportPage</code> implementation of this <code>Listener</code> method handles all events
    * and enablements for controls on this page. Subclasses may extend.
    * 
    * @param event Event
    */
   public void handleEvent(Event event) {
      setPageComplete(determinePageCompletion());

      updateWidgetEnablements();
   }

   /*
    * @see WizardPage#becomesVisible
    */
   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      // policy: wizards are not allowed to come up with an error message
      if (visible) {
         setErrorMessage(null);
      }
   }

   @Override
   protected void createOptionsGroup(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Outline Handlers");
      composite.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, true));
      composite.setLayout(new GridLayout(1, true));

      handlerList = new List(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
      handlerList.addListener(SWT.Selection, this);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 300;
      handlerList.setLayoutData(gridData);

      initList();
   }

   private void initList() {
      hasHandlers = false;
      for (IArtifactSourceParser parser : contributionManager.getArtifactSourceParser()) {
         for (IArtifactSourceParserDelegate delegate : contributionManager.getArtifactSourceParserDelegate(parser)) {
            hasHandlers = true;
            handlerList.add(delegate.getName());
            handlerList.setData(delegate.getName(), delegate);
            break;
         }
      }

      if (!hasHandlers) {
         handlerList.add("<No Handlers Installed>");
      }

      handlerList.getParent().pack(true);
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   public IArtifactSourceParserDelegate getSelectedOutlineContentHandler() {
      if (handlerList.getSelectionCount() == 1) {
         return (IArtifactSourceParserDelegate) handlerList.getData(handlerList.getSelection()[0]);
      } else {
         return null;
      }
   }

   @Override
   protected boolean validateOptionsGroup() {
      return hasHandlers && handlerList.getSelectionCount() == 1;
   }

}