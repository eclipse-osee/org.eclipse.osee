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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
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
public class AttributeTypePage extends WizardDataTransferPage implements Listener {
   public static final String PAGE_NAME = "osee.define.wizardPage.attributeTypePage";
   private List typeList;
   private boolean hasDescriptors;

   /**
    * @param descriptors Available descriptors to select from
    */
   public AttributeTypePage() {
      super(PAGE_NAME);

      hasDescriptors = false;
   }

   /**
    * (non-Javadoc) Method declared on IDialogPage.
    */
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
    * The <code>WizardResourceImportPage</code> implementation of this <code>Listener</code> method handles all
    * events and enablements for controls on this page. Subclasses may extend.
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
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      // policy: wizards are not allowed to come up with an error message
      if (visible) {
         setErrorMessage(null);
      }
   }

   protected void createOptionsGroup(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Attribute Types");
      composite.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, true));
      composite.setLayout(new GridLayout(1, true));

      typeList = new List(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
      typeList.addListener(SWT.Selection, this);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 300;
      typeList.setLayoutData(gridData);

   }

   /**
    * @param descriptors the descriptors to set
    */
   public void setDescriptors(Collection<AttributeType> descriptors) {
      java.util.List<AttributeType> sortedDescriptors =
            new ArrayList<AttributeType>(descriptors);
      Collections.sort(sortedDescriptors, new Comparator<AttributeType>() {
         public int compare(AttributeType o1, AttributeType o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
         }
      });

      String[] selection = typeList.getSelection();
      typeList.removeAll();

      hasDescriptors = !sortedDescriptors.isEmpty();
      if (hasDescriptors) {
         for (AttributeType descriptor : sortedDescriptors) {
            typeList.add(descriptor.getName());
            typeList.setData(descriptor.getName(), descriptor);
         }
      } else {
         typeList.add("<No Attribute Types>");
      }

      // Restore any prior selections
      typeList.setSelection(selection);
      typeList.getParent().pack(true);
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   public Collection<AttributeType> getSelectedAttributeDescriptors() {
      Collection<AttributeType> selectedAttributeDescriptors =
            new ArrayList<AttributeType>(typeList.getSelectionCount());

      if (hasDescriptors) {
         for (String attributeName : typeList.getSelection()) {
            selectedAttributeDescriptors.add((AttributeType) typeList.getData(attributeName));
         }
      }

      return selectedAttributeDescriptors;
   }

   @Override
   protected boolean validateOptionsGroup() {
      return hasDescriptors && typeList.getSelectionCount() > 0;
   }

}