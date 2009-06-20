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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Roberto E. Escobar
 */
public abstract class XSelectFromDialog<T> extends XText {

   private final List<T> selected;
   private final List<T> input;
   private int minSelectionRequired, maxSelectionRequired = 1;
   private Button selectionButton;

   public XSelectFromDialog(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
      this.selected = new ArrayList<T>();
      this.input = new ArrayList<T>();
      setToolTip("Click the button on the left to change the current selection.");
   }

   public XSelectFromDialog(String displayLabel) {
      this(displayLabel, "");
   }

   public void setRequiredSelection(int minSelectionRequired, int maxSelectionRequired) throws OseeArgumentException {
      if (minSelectionRequired < 0) {
         throw new OseeArgumentException("Min Number of Selection must be greater than or equal to 0");
      }
      if (maxSelectionRequired < 1) {
         throw new OseeArgumentException("Max Number of Selection must be at least 1");
      }

      if (maxSelectionRequired < minSelectionRequired) {
         throw new OseeArgumentException(String.format("Invalid required number of selections [%s] < [%s]",
               maxSelectionRequired, minSelectionRequired));
      }
      this.minSelectionRequired = minSelectionRequired;
      this.maxSelectionRequired = maxSelectionRequired;
   }

   @Override
   protected int getTextStyle() {
      return SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setToolTip(java.lang.String)
    */
   @Override
   public void setToolTip(String toolTip) {
      if (Strings.isValid(toolTip)) {
         super.setToolTip(toolTip);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#getData()
    */
   @Override
   public Object getData() {
      return getSelected();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#setEditable(boolean)
    */
   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (Widgets.isAccessible(selectionButton)) {
         selectionButton.setEnabled(editable);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#createWidgets(org.eclipse.swt.widgets.Composite, int, boolean)
    */
   @Override
   public void createControls(final Composite parent, int horizontalSpan, boolean fillText) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(3, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

      Label label = getLabelWidget();
      if (label != null) {
         label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
      }
      setVerticalLabel(true);
      super.createControls(composite, horizontalSpan, fillText);

      getStyledText().setEditable(false);
      getStyledText().setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

      selectionButton = new Button(composite, SWT.PUSH);
      selectionButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
      selectionButton.setText("Set...");
      selectionButton.setEnabled(isEditable());
      selectionButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (openSelectionDialog()) {
               notifyXModifiedListeners();
            }
         }
      });
      addToolTip(composite, getToolTip());
      getStyledText().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
      refresh();
   }

   private void addToolTip(Control control, String toolTipText) {
      if (Strings.isValid(toolTipText)) {
         control.setToolTipText(toolTipText);
         if (control instanceof Composite) {
            for (Control child : ((Composite) control).getChildren()) {
               child.setToolTipText(toolTipText);
            }
         }
      }
   }

   public void setSelectableItems(Collection<T> input) {
      this.input.clear();
      this.input.addAll(input);
   }

   public List<T> getSelectableItems() {
      return new ArrayList<T>(input);
   }

   public void setSelected(Collection<T> input) {
      this.selected.clear();
      this.selected.addAll(input);
      setText(Collections.toString("\n", selected));
   }

   public List<T> getSelected() {
      return new ArrayList<T>(selected);
   }

   public abstract CheckedTreeSelectionDialog createDialog();

   @SuppressWarnings("unchecked")
   protected boolean openSelectionDialog() {
      boolean selectedChanged = false;
      if (getSelectableItems().isEmpty()) {
         MessageDialog.openInformation(Display.getCurrent().getActiveShell(), getLabel(),
               "Could not find items available to select from.");
      } else {
         try {
            CheckedTreeSelectionDialog dialog = createDialog();
            dialog.setInitialElementSelections(getSelected());
            dialog.setInput(getSelectableItems());
            dialog.setValidator(new ISelectionStatusValidator() {

               @Override
               public IStatus validate(Object[] selection) {
                  IStatus status = null;
                  int numberSelected = selection.length;
                  if (minSelectionRequired <= numberSelected && maxSelectionRequired >= numberSelected) {
                     status = Status.OK_STATUS;
                  } else {
                     List<String> message = new ArrayList<String>();
                     if (numberSelected < minSelectionRequired) {
                        message.add(String.format("Must select at least [%s]", minSelectionRequired));
                     }
                     if (numberSelected > maxSelectionRequired) {
                        message.add(String.format("Can't select more than [%s]", maxSelectionRequired));
                     }
                     status =
                           new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, Collections.toString(" &&", message));
                  }
                  return status;
               }

            });
            int result = dialog.open();
            if (result == 0) {
               List<T> dialogSelections = new ArrayList<T>();
               for (Object obj : dialog.getResult()) {
                  dialogSelections.add((T) obj);
               }

               //               boolean wasDifference = !Collections.setComplement(selected, dialogSelections).isEmpty();
               //               wasDifference &= !Collections.setComplement(dialogSelections, selected).isEmpty();
               //               if (wasDifference) {
               setSelected(dialogSelections);
               selectedChanged = true;
               //               }
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return selectedChanged;
   }
}
