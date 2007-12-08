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
package org.eclipse.osee.framework.ui.skynet.util.email;

import java.sql.SQLException;
import java.util.ArrayList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.ui.skynet.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class EmailWizardPage extends WizardPage {
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private Text text;
   private ArrayList<Object> initialAddress;
   private TreeViewer namesList;
   private ListViewer toList;
   private ListViewer ccList;
   private ListViewer bccList;

   private final static String separator = "===============";
   private final ArrayList<EmailGroup> groups;

   /**
    * @param pageName
    * @param groups
    * @param initialAddress User, AtsEmailGroup or String
    */
   protected EmailWizardPage(String pageName, ArrayList<EmailGroup> groups, ArrayList<Object> initialAddress) {
      super(pageName);
      this.groups = groups;
      this.initialAddress = initialAddress;
   }

   @SuppressWarnings("unchecked")
   public void createControl(Composite parent) {
      setTitle("Email Action");

      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gl = new GridLayout();
      gl.numColumns = 2;
      composite.setLayout(gl);
      GridData gd = new GridData(GridData.FILL_BOTH);
      composite.setLayoutData(gd);

      Composite namesComp = new Composite(composite, SWT.NONE);
      namesComp.setLayout(new GridLayout());
      gd = new GridData(GridData.FILL_VERTICAL);
      namesComp.setLayoutData(gd);

      // Fill names array
      ArrayList<Object> names = new ArrayList<Object>();
      if (groups != null) {
         names.addAll(groups);
         if (groups.size() > 0) names.add(separator);
      }

      try {
         names.addAll(SkynetAuthentication.getInstance().getUsers());
         names.remove(skynetAuth.getUser(UserEnum.UnAssigned));
         names.remove(skynetAuth.getUser(UserEnum.NoOne));
      } catch (SQLException ex) {
         OSEELog.logException(getClass(), ex, false);
         names.add(ex.getLocalizedMessage());
      }

      namesList = new TreeViewer(namesComp);
      namesList.setContentProvider(new ArrayTreeContentProvider());
      namesList.setLabelProvider(new NamesLabelProvider());
      gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 75;
      namesList.getTree().setLayoutData(gd);
      namesList.getTree().setLinesVisible(false);
      namesList.setInput(names);
      namesList.getTree().addListener(SWT.MouseDoubleClick, new Listener() {
         public void handleEvent(Event event) {
            if (event.button == 1) {
               IStructuredSelection sel = (IStructuredSelection) namesList.getSelection();
               Object obj = sel.getFirstElement();
               if ((obj instanceof String) && ((String) obj).equals(separator)) return;
               toList.add(sel.getFirstElement());
            }
         }
      });

      Composite toComp = new Composite(composite, SWT.NONE);
      gl = new GridLayout();
      gl.numColumns = 2;
      toComp.setLayout(gl);
      gd = new GridData(GridData.FILL_BOTH);
      toComp.setLayoutData(gd);

      // Empty label to take up left column
      Label label = new Label(toComp, SWT.NONE);
      label = new Label(toComp, SWT.NONE);
      label.setText("(select and right-click to delete)");

      Button b = new Button(toComp, SWT.NONE);
      b.setText("To->   ");
      b.setSize(1000, 5);
      b.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            widgetDefaultSelected(e);
         }

         public void widgetDefaultSelected(SelectionEvent e) {
            IStructuredSelection sel = (IStructuredSelection) namesList.getSelection();
            for (Object obj : sel.toList())
               toList.add(obj);
         }
      });

      toList = new ListViewer(toComp);
      toList.setContentProvider(new ArrayContentProvider());
      toList.setLabelProvider(new NamesLabelProvider());
      gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 50;
      toList.getList().setLayoutData(gd);
      if (initialAddress != null) toList.setInput(initialAddress);
      toList.getList().setMenu(getDeletePopup(toList));

      b = new Button(toComp, SWT.NONE);
      b.setText("  Cc->   ");
      b.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            widgetDefaultSelected(e);
         }

         public void widgetDefaultSelected(SelectionEvent e) {
            IStructuredSelection sel = (IStructuredSelection) namesList.getSelection();
            for (Object obj : sel.toList())
               ccList.add(obj);
         }
      });

      ccList = new ListViewer(toComp);
      ccList.setContentProvider(new ArrayContentProvider());
      ccList.setLabelProvider(new NamesLabelProvider());
      gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 20;
      ccList.getList().setLayoutData(gd);
      ccList.getList().setMenu(getDeletePopup(ccList));

      b = new Button(toComp, SWT.NONE);
      b.setText("  Bcc->  ");
      b.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            widgetDefaultSelected(e);
         }

         public void widgetDefaultSelected(SelectionEvent e) {
            IStructuredSelection sel = (IStructuredSelection) namesList.getSelection();
            for (Object obj : sel.toList())
               bccList.add(obj);
         }
      });

      bccList = new ListViewer(toComp);
      bccList.setContentProvider(new ArrayContentProvider());
      bccList.setLabelProvider(new NamesLabelProvider());
      gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 20;
      bccList.getList().setLayoutData(gd);
      bccList.getList().setMenu(getDeletePopup(bccList));
      bccList.setInput(new Object[] {SkynetAuthentication.getInstance().getAuthenticatedUser().getEmail()});

      // Additional Text
      Label l = new Label(composite, SWT.NONE);
      l.setText("Additional Text:");
      gd = new GridData();
      gd.horizontalSpan = 2;
      l.setLayoutData(gd);

      text = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
      gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 2;
      gd.heightHint = 75;
      text.setLayoutData(gd);

      setControl(composite);
   }

   private Menu getDeletePopup(ListViewer listView) {
      Menu previewMenu = new Menu(listView.getControl());
      MenuItem item = new MenuItem(previewMenu, SWT.CASCADE);
      item.setText("Delete");
      final ListViewer fListView = listView;
      item.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection sel = (IStructuredSelection) fListView.getSelection();
            for (Object obj : sel.toList())
               fListView.remove(obj);
         }
      });

      return previewMenu;
   }

   public String[] getToAddresses() {
      return getEmails(toList);
   }

   public String[] getCcAddresses() {
      return getEmails(ccList);
   }

   public String[] getBccAddresses() {
      return getEmails(bccList);
   }

   public String[] getEmails(ListViewer list) {

      ArrayList<String> emails = new ArrayList<String>();
      for (int x = 0; x < list.getList().getItemCount(); x++) {
         Object obj = list.getElementAt(x);
         if (obj instanceof User)
            emails.add(((User) obj).getEmail());
         else if (obj instanceof String)
            emails.add(((String) obj));
         else if (obj instanceof EmailGroup) emails.addAll(((EmailGroup) obj).getEmails());
      }
      return emails.toArray(new String[emails.size()]);
   }

   public String getText() {
      return text.getText();
   }

   public class NamesLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object element) {
         if (element instanceof User)
            return ((User) element).getName();
         else if (element instanceof EmailGroup)
            return ((EmailGroup) element).toString();
         else if (element instanceof String) return ((String) element).toString();
         return "";
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }
   }
}
