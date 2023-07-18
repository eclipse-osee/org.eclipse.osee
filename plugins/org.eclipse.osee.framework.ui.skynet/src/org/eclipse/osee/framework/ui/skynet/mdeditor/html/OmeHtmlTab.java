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

package org.eclipse.osee.framework.ui.skynet.mdeditor.html;

import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.action.browser.IBrowserActionHandler;
import org.eclipse.osee.framework.ui.skynet.mdeditor.OmeAbstractTab;
import org.eclipse.osee.framework.ui.skynet.mdeditor.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.mdeditor.model.ArtOmeData;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextOseeImageLinkListener;
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
      String mdContent = omeData.getMdContent();
      if (Strings.isInValid(mdContent)) {
         omeData.load();
         mdContent = omeData.getMdContent();
      }
      if (Strings.isValid(mdContent)) {
         // Converting oseelink tags to html
         Matcher m1 = XTextOseeLinkListener.oseeLinkPattern.matcher(mdContent);
         while (m1.find()) {
            String idStr = m1.group(1);
            String name = m1.group(2);
            String url = String.format("<a href=\"%s\">%s</a>", idStr, name);
            mdContent = mdContent.replaceFirst("<oseelink>(.*?)</oseelink>", url);
         }

         // Converting oseeimagelink tags to html
         Matcher m2 = XTextOseeImageLinkListener.oseeImageLinkPattern.matcher(mdContent);
         while (m2.find()) {
            String idStr = m2.group(1);
            String name = m2.group(2);

            if (omeData instanceof ArtOmeData) {
               // If "Display Images" option is toggled
               if (((ArtOmeData) omeData).getDisplayImagesBool()) {

                  // Save the image artifact to the .preview folder in osee.data
                  BranchToken imageArtifactBranch = ((ArtOmeData) omeData).getArtifact().getBranch();

                  /*
                   * Build the rest call to place within the image tag. Rest call requires server, branch id, artifact
                   * id (general document artifact), attribute type id (native content attribute)
                   */
                  String imageContentRestCall = String.format("%s/orcs/branch/%s/artifact/%s/attribute/type/%s",
                     System.getProperty("osee.application.server"), imageArtifactBranch.getId(), idStr,
                     CoreAttributeTypes.NativeContent.getId());

                  // Create the image tag with the file location specified
                  String imgTag = String.format("<img src=\"%s\" alt=\"%s\">", imageContentRestCall, name);

                  mdContent = mdContent.replaceFirst("<oseeimagelink>(.*?)</oseeimagelink>", imgTag);

               } else {
                  // Create normal href link to the artifact
                  String hrefImageArt = String.format("<a href=\"%s\">%s</a>", idStr, name);

                  mdContent = mdContent.replaceFirst("<oseeimagelink>(.*?)</oseeimagelink>", hrefImageArt);
               }
            }
         }

         MutableDataSet options = new MutableDataSet();
         options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), TaskListExtension.create()));
         Parser parser = Parser.builder(options).build();
         HtmlRenderer renderer = HtmlRenderer.builder(options).build();
         Node document = parser.parse(mdContent);
         String html = renderer.render(document);
         String styledHtml = addHTMLStyling(html);

         omeData.setHtmlContent(styledHtml);
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               browser.setText(styledHtml);
               if (managedForm != null) {
                  managedForm.reflow(true);
               }
            }
         });
      }
   }

   private static String addHTMLStyling(String html) {
      String style =
         "<style> table { width: 100%; } table, th, td { border: 1px solid black; border-collapse: collapse; padding: 5px; }";
      style += "img { display: block; margin-left: auto; margin-right: auto; }";
      style += "</style>";
      return style + html;
   }

   private static String execCmd(String cmd) throws java.io.IOException {
      java.util.Scanner s = null;
      InputStream runtime = null;
      String html = "";
      try {
         runtime = Runtime.getRuntime().exec(cmd).getInputStream();
         s = new java.util.Scanner(runtime).useDelimiter("\\A");
         if (s.hasNext()) {
            html = s.next();
         }
      } finally {
         if (s != null) {
            s.close();
         }
         if (runtime != null) {
            runtime.close();
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
