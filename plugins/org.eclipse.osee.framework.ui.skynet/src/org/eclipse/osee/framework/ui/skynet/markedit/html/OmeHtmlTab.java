/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.ui.skynet.markedit.html;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.action.browser.IBrowserActionHandler;
import org.eclipse.osee.framework.ui.skynet.markedit.OmeAbstractTab;
import org.eclipse.osee.framework.ui.skynet.markedit.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextOseeLinkListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Donald G. Dunne
 */
public class OmeHtmlTab extends OmeAbstractTab implements IBrowserActionHandler {

   private OmeHtmlComposite htmlComposite;
   private IManagedForm managedForm;

   public OmeHtmlTab(FormEditor editor, AbstractOmeData omeData) {
      super(editor, "ome.editor.preview", omeData, "Markdown Preview");
   }

   @Override
   public void handleRefreshAction() {
      handleRefreshAction(omeData, getBrowser(), managedForm);
   }

   public static void handleRefreshAction(AbstractOmeData omeData, Browser browser, IManagedForm managedForm) {
      try {
         String mdContent = omeData.getMdContent();
         if (Strings.isInValid(mdContent)) {
            omeData.load();
            mdContent = omeData.getMdContent();
         }
         if (Strings.isValid(mdContent)) {
            Matcher m = XTextOseeLinkListener.oseeLinkPattern.matcher(mdContent);
            while (m.find()) {
               String idStr = m.group(1);
               String name = m.group(2);
               String url = String.format("<a href=\"%s\">%s</a>", idStr, name);
               mdContent = mdContent.replaceFirst("<oseelink>(.*?)</oseelink>", url);
            }

            File mdFile = new File("mdFile.md");
            Lib.writeStringToFile(mdContent, mdFile);
            String cmd = "C:/Tools/mdconv/comrak-v0.13.1.exe " //
               + "--config-file C:/Tools/mdconv/comrak_config " //
               + mdFile.getAbsolutePath();
            String html = execCmd(cmd);
            omeData.setHtmlContent(html);
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  browser.setText(html);
                  if (managedForm != null) {
                     managedForm.reflow(true);
                  }
               }
            });
         }
      } catch (IOException ex) {
         System.err.println(Lib.exceptionToString(ex));
      }
   }

   private static String execCmd(String cmd) throws java.io.IOException {
      java.util.Scanner s = null;
      String html = "";
      try {
         s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
         if (s.hasNext()) {
            html = s.next();
         }
      } finally {
         if (s != null) {
            s.close();
         }
      }
      return html;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      this.managedForm = managedForm;
      super.createFormContent(managedForm);
      try {
         updateTitleBar(managedForm);

         bodyComp = managedForm.getForm().getBody();
         GridLayout gridLayout = new GridLayout(1, false);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, false);
         gd.widthHint = 300;
         bodyComp.setLayoutData(gd);

         updateTitleBar(managedForm);
         createToolbar(managedForm);

         htmlComposite = new OmeHtmlComposite(bodyComp, SWT.BORDER, omeData);
         htmlComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
         htmlComposite.handleRefreshAction(omeData);

         XWidgetUtility.addMessageDecoration(managedForm, managedForm.getForm());
         FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);
      } catch (Exception ex) {
         handleException(ex);
      }
   }

   @Override
   public Browser getBrowser() {
      return htmlComposite.getBrowser();
   }

   @Override
   public String getTabName() {
      return "MD Preview";
   }

}
