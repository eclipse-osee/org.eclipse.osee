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

package org.eclipse.osee.framework.ui.skynet.search;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for performing find/replace of attribute values on a set of <code>Artifact</code>'s.
 *
 * @see org.eclipse.osee.framework.skynet.core.artifact.Artifact
 * @see org.eclipse.osee.framework.skynet.core.artifact.Attribute
 * @author Robert A. Fisher
 */
public class AttributeFindReplaceDialog extends Dialog {
   private ComboViewer cmbAttributeDescriptors;
   private Text txtFindRegEx;
   private Text txtReplaceStr;
   private BranchId branch;

   private final List<Artifact> artifacts;

   public AttributeFindReplaceDialog(Shell parentShell, List<Artifact> artifacts) {
      super(parentShell);

      this.artifacts = artifacts;
      if (artifacts != null && !artifacts.isEmpty()) {
         this.branch = artifacts.get(0).getBranch();
      }
      setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation() | SWT.RESIZE);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      getShell().setText("Find/Replace Attribute Value");

      Composite mainComposite = new Composite(parent, SWT.NONE);
      mainComposite.setFont(parent.getFont());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComposite.setLayout(new GridLayout(1, false));

      addDialogControls(mainComposite);
      addListeners();
      setInputs();

      return mainComposite;
   }

   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      super.createButtonsForButtonBar(parent);
      checkEnabled();
   }

   private void setInputs() {
      try {
         Collection<AttributeTypeToken> attributeTypes = AttributeTypeManager.getValidAttributeTypes(branch);
         cmbAttributeDescriptors.setInput(attributeTypes.toArray(new AttributeTypeToken[attributeTypes.size()]));
         cmbAttributeDescriptors.getCombo().select(0);
      } catch (OseeCoreException ex) {
         cmbAttributeDescriptors.setInput(new Object[] {ex});
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void addListeners() {
      txtFindRegEx.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            checkEnabled();
         }
      });
   }

   private void addDialogControls(Composite mainComposite) {
      Label label;
      label = new Label(mainComposite, SWT.LEFT);
      label.setText("Attribute Type");
      label.setToolTipText("The attribute to perform the find/replace logic against");

      cmbAttributeDescriptors = new ComboViewer(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
      cmbAttributeDescriptors.setContentProvider(new ArrayContentProvider());
      cmbAttributeDescriptors.setLabelProvider(new ArtifactTypeLabelProvider());
      cmbAttributeDescriptors.setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare(((AttributeType) e1).getName(), ((AttributeType) e2).getName());
         }
      });

      label = new Label(mainComposite, SWT.LEFT);
      label.setText("Find (regex):");
      label.setToolTipText("The regular expression to perform matching with against the attribute value");
      txtFindRegEx = new Text(mainComposite, SWT.BORDER);

      label = new Label(mainComposite, SWT.LEFT);
      label.setText("Replace With:");
      label.setToolTipText("The value to put in place of the value matched by the Find regular expression");
      txtReplaceStr = new Text(mainComposite, SWT.BORDER);

   }

   private void checkEnabled() {
      boolean enable = txtFindRegEx.getText().length() > 0 && !artifacts.isEmpty();
      getButton(IDialogConstants.OK_ID).setEnabled(enable);
   }

   @Override
   protected void okPressed() {
      final Pattern pattern = Pattern.compile(txtFindRegEx.getText());
      final String replaceText = txtReplaceStr.getText();
      final AttributeTypeId attributeType =
         (AttributeTypeId) ((IStructuredSelection) cmbAttributeDescriptors.getSelection()).getFirstElement();

      Job job = new Job("Find/Replace") {

         @Override
         protected IStatus run(final IProgressMonitor monitor) {
            IStatus toReturn = Status.CANCEL_STATUS;
            try {
               monitor.beginTask("Find/Replace " + attributeType + " Attribute Value", artifacts.size());

               for (Artifact artifact : artifacts) {
                  monitor.subTask("Modifying " + artifact.getName());
                  for (Attribute<?> attribute : artifact.getAttributes(attributeType)) {
                     Matcher matcher = pattern.matcher(attribute.toString());
                     attribute.setFromString(matcher.replaceAll(replaceText));
                  }
                  monitor.worked(1);
                  if (monitor.isCanceled()) {
                     throw new IllegalStateException("USER_PURPLE CANCELLED");
                  }
               }
               Artifacts.persistInTransaction("Attribute find replace dialog", artifacts);
               toReturn = Status.OK_STATUS;
            } catch (Exception ex) {
               if (ex.getMessage().equals("USER_PURPLE CANCELLED")) {
                  toReturn = Status.CANCEL_STATUS;
               } else {
                  toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.OK, ex.getMessage(), ex);
               }
            } finally {
               monitor.done();
            }

            return toReturn;
         }
      };
      Jobs.startJob(job);
      super.okPressed();
   }
   private static class ArtifactTypeLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object element) {
         return null;
      }

      @Override
      public String getText(Object element) {
         if (element instanceof AttributeType) {
            return ((AttributeType) element).getName();
         } else {
            return element.toString();
         }
      }

      @Override
      public void addListener(ILabelProviderListener listener) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object element, String property) {
         return true;
      }

      @Override
      public void removeListener(ILabelProviderListener listener) {
         // do nothing
      }
   }
}
