/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.artifact.editor.parts;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Roberto E. Escobar
 */
public class MessageSummaryNote {
   private final Shell shell;

   public MessageSummaryNote(IManagedForm managedForm, String title, IMessage[] messages) {
      final ScrolledForm form = managedForm.getForm();
      final FormToolkit toolkit = managedForm.getToolkit();

      shell = new Shell(form.getShell(), SWT.ON_TOP | SWT.TOOL);
      shell.setImage(getImage(form.getMessageType()));
      shell.setText(title);
      shell.setLayout(new FillLayout());

      Composite composite = toolkit.createComposite(shell, toolkit.getBorderStyle());
      composite.setLayout(new GridLayout());

      FormText text = toolkit.createFormText(composite, true);
      configureFormText(form.getForm(), text);
      text.setText(getMessageSummary(messages), true, false);
      text.addFocusListener(new FocusAdapter() {

         @Override
         public void focusLost(FocusEvent e) {
            shell.close();
         }
      });
      shell.setLocation(0, 0);
   }

   public void setLocation(Point point) {
      shell.setLocation(point);
   }

   public void open() {
      shell.pack();
      shell.open();
   }

   private void configureFormText(final Form form, FormText text) {
      text.addHyperlinkListener(new HyperlinkAdapter() {
         @Override
         public void linkActivated(HyperlinkEvent e) {
            String is = (String) e.getHref();
            try {
               int index = Integer.parseInt(is);
               IMessage[] messages = form.getChildrenMessages();
               IMessage message = messages[index];
               Control c = message.getControl();
               ((FormText) e.widget).getShell().dispose();
               if (c != null) {
                  c.setFocus();
               }
            } catch (NumberFormatException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
      text.setImage("error", getImage(IMessageProvider.ERROR));
      text.setImage("warning", getImage(IMessageProvider.WARNING));
      text.setImage("info", getImage(IMessageProvider.INFORMATION));
   }

   private Image getImage(int type) {
      Image image = null;
      switch (type) {
         case IMessageProvider.ERROR:
            image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            break;
         case IMessageProvider.WARNING:
            image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
            break;
         case IMessageProvider.INFORMATION:
            image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
            break;
         default:
            break;
      }
      return image;
   }

   private String getMessageSummary(IMessage[] messages) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println("<form>");
      for (int i = 0; i < messages.length; i++) {
         IMessage message = messages[i];
         pw.print("<li vspace=\"false\" style=\"image\" indent=\"16\" value=\"");
         switch (message.getMessageType()) {
            case IMessageProvider.ERROR:
               pw.print("error");
               break;
            case IMessageProvider.WARNING:
               pw.print("warning");
               break;
            case IMessageProvider.INFORMATION:
               pw.print("info");
               break;
         }
         pw.print("\"> <a href=\"");
         pw.print(i + "");
         pw.print("\">");
         if (message.getPrefix() != null) {
            pw.print(message.getPrefix());
         }
         pw.print(message.getMessage());
         pw.println("</a></li>");
      }
      pw.println("</form>");
      pw.flush();
      return sw.toString();
   }
}
