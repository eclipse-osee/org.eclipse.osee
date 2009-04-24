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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage.Location;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IActionContributor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.parts.AttributeFormPart;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class AttributesFormSection extends ArtifactEditorFormSection {

   private IActionContributor actionContributor;

   public AttributesFormSection(NewArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
    */
   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      Section section = getSection();
      section.setText("Attributes");
      section.setLayout(new GridLayout(1, false));
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      addToolBar(form);

      Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout());
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      AttributeFormPart detailsPage = new AttributeFormPart(getEditor());
      form.addPart(detailsPage);
      detailsPage.createContents(sectionBody);
      toolkit.getBorderStyle();
      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);
   }

   public void addToolBar(IManagedForm form) {
      final FormToolkit toolkit = form.getToolkit();
      Composite composite = toolkit.createComposite(getSection());
      composite.setLayout(ALayout.getZeroMarginLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      ToolBar toolBar = new ToolBar(composite, SWT.FLAT);
      ToolBarManager manager = new ToolBarManager(toolBar);

      getActionContributor().contributeToToolBar(manager);
      manager.update(true);

      getSection().setTextClient(composite);
   }

   private IActionContributor getActionContributor() {
      if (actionContributor == null) {
         actionContributor = new AttributesDataActionContribution();
      }
      return actionContributor;
   }

   private final class AttributesDataActionContribution implements IActionContributor {

      public void contributeToToolBar(IToolBarManager manager) {
         manager.add(new OpenAddAttributeTypeDialogAction());
         manager.add(new OpenDeleteAttributeTypeDialogAction());
      }

      private final class OpenAddAttributeTypeDialogAction extends Action {
         public OpenAddAttributeTypeDialogAction() {
            super();
            ImageDescriptor expandAll = SkynetGuiPlugin.getInstance().getImageDescriptor("add.gif");
            setImageDescriptor(expandAll);
            setToolTipText("Opens a dialog to select which attribute type instances to create on the artifact");
         }

         public void run() {
            System.out.println("created");
            //            CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), );
         }
      }

      private final class OpenDeleteAttributeTypeDialogAction extends Action {
         public OpenDeleteAttributeTypeDialogAction() {
            super();
            ImageDescriptor expandAll = SkynetGuiPlugin.getInstance().getImageDescriptor("delete.gif");
            setImageDescriptor(expandAll);
            setToolTipText("Opens a dialog to select which attribute type instances to remove from the artifact");
         }

         public void run() {
            System.out.println("deleted");
         }
      }

      private final class DefaultExpandedAction extends Action {

         public DefaultExpandedAction() {
            super();
            Image expandAll = SkynetGuiPlugin.getInstance().getImage("expandAll.gif");
            ImageDescriptor defaultOverlay = SkynetGuiPlugin.getInstance().getImageDescriptor("switched.gif");
            OverlayImage overlayImage = new OverlayImage(expandAll, defaultOverlay, Location.TOP_RIGHT);
            setImageDescriptor(ImageDescriptor.createFromImage(overlayImage.createImage()));
            setToolTipText("Expands sections with data");
         }

         public void run() {
            //         setDefaultSectionExpantion();
            //         scrolledForm.getBody().layout();
         }
      }

      private final class ExpandAllAction extends Action {

         public ExpandAllAction() {
            super();
            ImageDescriptor expandAll = SkynetGuiPlugin.getInstance().getImageDescriptor("expandAll.gif");
            setImageDescriptor(expandAll);
            setToolTipText("Expands all sections");
         }

         public void run() {
            //         for (ExpandableComposite expandable : expandableItems) {
            //            expandable.setExpanded(true);
            //         }
            //         scrolledForm.getBody().layout();
         }
      }

      private final class CollapseAllAction extends Action {

         public CollapseAllAction() {
            super();
            ImageDescriptor collapsedAll = SkynetGuiPlugin.getInstance().getImageDescriptor("collapseAll.gif");
            setImageDescriptor(collapsedAll);
            setToolTipText("Collapses all sections");
         }

         public void run() {
            //         for (ExpandableComposite expandable : expandableItems) {
            //            expandable.setExpanded(false);
            //         }
            //         scrolledForm.getBody().layout();
         }
      }
   }
}
