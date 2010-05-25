/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch.action;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.ui.message.internal.Activator;
import org.eclipse.osee.ote.ui.message.tree.ElementNode;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Ken J. Aguilar
 */
public class SetValueAction extends Action {
   private final WatchedMessageNode msgNode;
   private final ElementNode node;

   public SetValueAction(ElementNode node) {
      super("Set Value");
      this.node = node;
      msgNode = (WatchedMessageNode) node.getMessageNode();
      setEnabled(node.isEnabled() && msgNode.getSubscription().getMessageMode() == MessageMode.WRITER && msgNode.getSubscription().isActive());
   }

   @Override
   public void run() {
      Message msg = msgNode.getSubscription().getMessage();
      List<Object> path = node.getElementPath().getElementPath();
      Element element = msg.getElement(path);
      if (element instanceof EnumeratedElement) {
         final EnumeratedElement<?> enumElement = (EnumeratedElement<?>) element;
         try {
            final Enum<?>[] values = enumElement.getEnumValues();
            int width = 0;
            for (Enum<?> val : values) {
               width = val.toString().length() > width ? val.toString().length() : width;
            }

            final ListDialog dialog = new ListDialog(Display.getDefault().getActiveShell());
            dialog.setInput(values);
            dialog.setTitle("Set Value");
            dialog.setAddCancelButton(true);
            dialog.setWidthInChars(width + 5);
            dialog.setMessage(element.getFullName() + "\nSelect New Value");
            dialog.setContentProvider(new ITreeContentProvider() {

               public Object[] getChildren(Object parentElement) {
                  return null;
               }

               public Object getParent(Object element) {
                  return values;
               }

               public boolean hasChildren(Object element) {
                  return false;
               }

               public Object[] getElements(Object inputElement) {
                  return values;
               }

               public void dispose() {
               }

               public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
               }

            });

            dialog.setLabelProvider(new LabelProvider() {
               @Override
               public boolean isLabelProperty(Object element, String property) {
                  return false;
               }
            });

            int result = dialog.open();
            if (result == Window.OK) {
               final Object[] objs = dialog.getResult();
               if (objs.length == 1) {
                  final String value = ((Enum<?>) objs[0]).name();
                  msgNode.getSubscription().setElementValue(path, value);
               }
            }
         } catch (Throwable t) {
            final String logMsg =
                  String.format("Exception while attempting to set element %s of message %s", element.getName(),
                        msg.getName());
            OseeLog.log(Activator.class, Level.SEVERE, logMsg, t);
         }

      } else {
         InputDialog dialog =
               new InputDialog(Display.getDefault().getActiveShell(), "Set " + element.getFullName(),
                     "Enter set value", "0", new IInputValidator() {

                        @Override
                        public String isValid(String newText) {
                           // No Error accept all values;
                           return null;
                        }
                     });

         //         final EntryDialog dialog =
         //               new EntryDialog(Display.getDefault().getActiveShell(), "Set " + element.getFullName(), null,
         //                     "Enter set value", MessageDialog.QUESTION, new String[] {"Ok", "Cancel"}, 0);
         if (dialog.open() == 0) {
            try {
               final String val = dialog.getValue();
               msgNode.getSubscription().setElementValue(path, val);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, String.format(
                     "Unable to set the %s element for the message %s", element.getName(), msg.getName()), ex);
               MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Could not set value");
            }
         }
      }
   }
}
