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
package org.eclipse.osee.define.ide.traceability.operations;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.define.ide.traceability.operations.TraceResourceDropOperation.RenameConfirmer;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class UiRenameConfirmer implements RenameConfirmer {

   @Override
   public boolean acceptUpdate(final Map<Artifact, String> nameUpdateRequired) {
      final MutableBoolean result = new MutableBoolean(false);
      if (nameUpdateRequired != null && !nameUpdateRequired.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               try {
                  MessageDialog dialog = createDialog(nameUpdateRequired);
                  int value = dialog.open();
                  result.setValue(value == Window.OK);
               } catch (Throwable ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Error during UI rename", ex);
                  result.setValue(false);
               }
            }
         }, true);
      } else {
         result.setValue(true);
      }
      return result.getValue();
   }

   private MessageDialog createDialog(final Map<Artifact, String> nameUpdateRequired) {
      MessageDialog dialog = new XTableDialog(Displays.getActiveShell(), "Rename Artifacts", null,
         "The following artifacts will be renamed.\n\n Select OK to continue with rename or Cancel to abort.",
         MessageDialog.CONFIRM, new String[] {"Ok", "Cancel"}, 0, nameUpdateRequired);
      return dialog;
   }

   private static final class XTableDialog extends MessageDialog {

      private final Map<Artifact, String> nameUpdateRequired;

      public XTableDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, Map<Artifact, String> nameUpdateRequired) {
         super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
            defaultIndex);
         setShellStyle(getShellStyle() | SWT.RESIZE);
         this.nameUpdateRequired = nameUpdateRequired;
      }

      @Override
      protected Control createCustomArea(Composite parent) {
         Composite area = new Composite(parent, SWT.BORDER);
         area.setLayout(new GridLayout());
         area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         XTable table = new XTable(area, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
         table.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         table.setContentProvider(new XTableContentProvider());
         table.setLabelProvider(new XTableLabelProvider(table));
         table.setInput(nameUpdateRequired);
         return area;
      }
   }

   private static final class XTable extends XViewer {
      public XTable(Composite parent, int style) {
         super(parent, style, new XTableFactory());
      }
   }

   private static final class XTableFactory extends XViewerFactory {
      private static String COLUMN_NAMESPACE = "xviewer.rename.dialog.table";

      public static XViewerColumn FROM_NAME_COLUMN = new XViewerColumn(COLUMN_NAMESPACE + ".from.name", "From Name",
         200, XViewerAlign.Left, true, SortDataType.String, false, null);
      public static XViewerColumn TO_NAME_COLUMN = new XViewerColumn(COLUMN_NAMESPACE + ".to.name", "To Name", 200,
         XViewerAlign.Left, true, SortDataType.String, false, null);

      public XTableFactory() {
         super(COLUMN_NAMESPACE);

         registerColumns(FROM_NAME_COLUMN, TO_NAME_COLUMN);
      }

      @Override
      public boolean isAdmin() {
         return false;
      }
   }

   private static final class XTableLabelProvider extends XViewerLabelProvider {

      public XTableLabelProvider(XViewer xViewer) {
         super(xViewer);
      }

      @SuppressWarnings("unchecked")
      @Override
      public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
         Entry<Artifact, String> item = (Entry<Artifact, String>) element;
         if (xCol.equals(XTableFactory.FROM_NAME_COLUMN)) {
            return item.getKey().getSafeName();
         }
         if (xCol.equals(XTableFactory.TO_NAME_COLUMN)) {
            return item.getValue();
         }
         return "unhandled column";
      }

      @Override
      public void dispose() {
         //
      }

      @Override
      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      @Override
      public void addListener(ILabelProviderListener listener) {
         //
      }

      @Override
      public void removeListener(ILabelProviderListener listener) {
         //
      }

      @Override
      public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
         return null;
      }
   }

   public static final class XTableContentProvider extends ArrayTreeContentProvider {

      @Override
      public Object[] getChildren(Object parentElement) {
         if (parentElement instanceof Map) {
            return ((Map<?, ?>) parentElement).entrySet().toArray();
         } else if (parentElement instanceof Entry) {
            Entry<?, ?> entry = (Entry<?, ?>) parentElement;
            return new Object[] {entry.getKey(), entry.getValue()};
         }
         return new Object[] {};
      }

   }

}
