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
package org.eclipse.osee.framework.ui.skynet.replace;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceArtifactWithBaselineOperation;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceAttributeWithBaselineOperation;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceRelationsWithBaselineOperation;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceWithBaselineVersionDialog extends MessageDialog {
   private ArrayList<AbstractOperation> operations;
   private boolean replaceAttributeSelected;
   private boolean replaceAllRelationsSelected;
   private boolean replaceAllAttributesSelected;

   private final Collection<Artifact> artifacts;
   private final Collection<Attribute<?>> attributes;

   public ReplaceWithBaselineVersionDialog(String title, Collection<Artifact> artifacts, Collection<Attribute<?>> attributes) {
      super(Displays.getActiveShell(), title, null, null, MessageDialog.NONE, new String[] {"Ok", "Cancel"}, 0);
      setShellStyle(getShellStyle() | SWT.RESIZE);

      this.artifacts = artifacts;
      this.attributes = attributes;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Composite composite = new Composite(container, SWT.NONE);
      new Label(composite, SWT.NONE).setText("Select the object(s) to be replaced with their baseline values");
      new Label(composite, SWT.NONE);
      new Label(composite, SWT.NONE);
      new Label(composite, SWT.NONE);
      final Button replaceSelectedAttribute = new Button(composite, SWT.CHECK);
      replaceSelectedAttribute.setText("Replace a single Attribute");
      replaceSelectedAttribute.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            replaceAttributeSelected = replaceSelectedAttribute.getEnabled();
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            //Do nothing
         }
      });
      final Button replaceAllRelations = new Button(composite, SWT.CHECK);
      replaceAllRelations.setText("Replace all Relations");
      replaceAllRelations.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            replaceAllRelationsSelected = replaceAllRelations.getEnabled();
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            //Do nothing
         }
      });
      final Button replaceAllAttributes = new Button(composite, SWT.CHECK);
      replaceAllAttributes.setText("Replace all Attributes");
      replaceAllAttributes.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            replaceAllAttributesSelected = replaceAllAttributes.getEnabled();
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            //Do nothing
         }
      });

      composite.setLayout(ALayout.getZeroMarginLayout());
      composite.setLayoutData(new GridData(GridData.FILL_BOTH));

      return composite.getShell();
   }

   private void handleUserRequest() {
      operations = new ArrayList<AbstractOperation>();

      if (replaceAttributeSelected && !replaceAllAttributesSelected && attributes != null) {
         operations.add(new ReplaceAttributeWithBaselineOperation(attributes));
      }
      if (replaceAllAttributesSelected && artifacts != null) {
         operations.add(new ReplaceArtifactWithBaselineOperation(artifacts));
      }
      if (replaceAllRelationsSelected && artifacts != null) {
         operations.add(new ReplaceRelationsWithBaselineOperation(artifacts));
      }
   }

   public Collection<AbstractOperation> getOperations() {
      handleUserRequest();
      return operations;
   }
}
