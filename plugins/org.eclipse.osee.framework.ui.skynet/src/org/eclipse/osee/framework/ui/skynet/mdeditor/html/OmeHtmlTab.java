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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.ui.skynet.action.browser.IBrowserActionHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.mdeditor.OmeAbstractTab;
import org.eclipse.osee.framework.ui.skynet.mdeditor.model.AbstractOmeData;
import org.eclipse.osee.framework.ui.skynet.mdeditor.model.ArtOmeData;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
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

      if (Strings.isValid(mdContent) && omeData instanceof ArtOmeData) {
         // Converting osee-artifact link tags to html
         Matcher oseeLinkMatcher = XTextOseeLinkListener.oseeLinkPattern.matcher(mdContent);
         Set<ArtifactId> artifactLinkIds = new HashSet<>();

         while (oseeLinkMatcher.find()) {
            artifactLinkIds.add(ArtifactId.valueOf(oseeLinkMatcher.group(1)));
         }

         BranchToken artifactBranch = ((ArtOmeData) omeData).getArtifact().getBranch();
         List<Artifact> linkArtifacts =
            ArtifactQuery.getArtifactListFrom(artifactLinkIds, artifactBranch, DeletionFlag.EXCLUDE_DELETED);

         for (Artifact art : linkArtifacts) {
            String artId = art.getIdString();
            String name = art.getSoleAttributeValue(CoreAttributeTypes.Name);
            String url = String.format("<a href=\"%s\">%s</a>", artId, name);
            String tagToReplace = "<osee-artifact>" + artId + "</osee-artifact>";
            mdContent = mdContent.replace(tagToReplace, url);
         }

         // Converting osee-image tags to html
         Matcher imageLinkMatcher = XTextOseeImageLinkListener.oseeImageLinkPattern.matcher(mdContent);
         Set<ArtifactId> imageLinkIds = new HashSet<>();

         while (imageLinkMatcher.find()) {
            imageLinkIds.add(ArtifactId.valueOf(imageLinkMatcher.group(1)));
         }

         List<Artifact> imageArtifacts =
            ArtifactQuery.getArtifactListFrom(imageLinkIds, artifactBranch, DeletionFlag.EXCLUDE_DELETED);

         for (Artifact art : imageArtifacts) {
            String artId = art.getIdString();
            String tagToReplace = "<osee-image>" + artId + "</osee-image>";
            // If "Display Images" option is toggled
            if (((ArtOmeData) omeData).getDisplayImagesBool()) {
               FileSystemRenderer renderer = new NativeRenderer();
               InputStream inputStream = null;

               try {
                  inputStream = renderer.getRenderInputStream(PresentationType.DEFAULT_OPEN, Arrays.asList(art));
                  byte[] imageBytes = Lib.inputStreamToBytes(inputStream);
                  String base64image = Base64.getEncoder().encodeToString(imageBytes);
                  String extension = art.getSoleAttributeValue(CoreAttributeTypes.Extension);

                  String imgTag = "<img src=\"data:image/" + extension + ";base64," + base64image + "\" />";
                  mdContent = mdContent.replace(tagToReplace, imgTag);

               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               } finally {
                  if (inputStream != null) {
                     try {
                        inputStream.close();
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
               }
            } else {
               String name = art.getSoleAttributeValue(CoreAttributeTypes.Name);
               String hrefImageArt = String.format("<a href=\"%s\">%s</a>", artId, name);
               mdContent = mdContent.replace(tagToReplace, hrefImageArt);
            }
         }

         try {
            String html = PublishingRequestHandler.convertMarkdownToHtml(mdContent);
            omeData.setHtmlContent(html);
            Displays.ensureInDisplayThread(() -> {
               browser.setText(html);
               if (managedForm != null) {
                  managedForm.reflow(true);
               }
            });
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
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
