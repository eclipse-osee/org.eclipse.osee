/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.links;

import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class EditLinkDialog extends EntryCheckDialog {

   private XText urlTextWidget, tagTextWidget;
   private String nameText, urlText = "", tagText = "";
   private final String label2;
   private Listener okListener;
   private Link link;

   public EditLinkDialog(Link link) {
      this();
      this.link = link;
      urlText = link.getUrl();
      tagText = Collections.toString(", ", link.getTags());
      nameText = link.getName();
      setEntry(nameText);
   }

   public EditLinkDialog() {
      super("Enter Link", "Create/Update New Link", "Share with others");
      super.setLabel("Link Name");
      super.setTextHeight(100);
      this.label2 = "URL";
   }

   @Override
   protected void createExtendedArea(Composite parent) {
      text.getLabelWidget().addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            if (e.button == 3) {
               text.setText("Google");
               urlTextWidget.setText("http://www.google.com");
               tagTextWidget.setText("Search");
            }
         }

      });

      urlTextWidget = new XText(label2);
      urlTextWidget.setFillHorizontally(true);
      if (isFillVertically()) {
         urlTextWidget.setFillVertically(true);
         urlTextWidget.setHeight(100);
         text.setHeight(100);
         urlTextWidget.setFont(getFont());
      }
      urlTextWidget.set(urlText);
      urlTextWidget.createWidgets(parent, 1);

      urlTextWidget.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            handleModified();
            urlText = urlTextWidget.get();
         }
      });

      tagTextWidget = new XText("Enter Search Tags (comma sperated)");
      tagTextWidget.setFillHorizontally(true);
      if (isFillVertically()) {
         tagTextWidget.setFillVertically(true);
         tagTextWidget.setHeight(100);
         text.setHeight(100);
         tagTextWidget.setFont(getFont());
      }
      tagTextWidget.set(tagText);
      tagTextWidget.createWidgets(parent, 1);

      tagTextWidget.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            handleModified();
            tagText = tagTextWidget.get();
         }
      });

      super.createExtendedArea(parent);
   }

   public String getUrl() {
      return urlText;
   }

   public String getTags() {
      return tagText;
   }

   public void setUrl(String url) {
      if (urlTextWidget != null) {
         urlTextWidget.set(url);
      }
      this.urlText = url;
   }

   public void setTags(String tags) {
      if (urlTextWidget != null) {
         urlTextWidget.set(tags);
      }
      this.urlText = tags;
   }

   public void setOkListener(Listener okListener) {
      this.okListener = okListener;
   }

   @Override
   protected void buttonPressed(int buttonId) {
      super.buttonPressed(buttonId);
      if (buttonId == 0 && okListener != null) {
         okListener.handleEvent(null);
      }
   }

   @Override
   public boolean isEntryValid() {
      if (!super.isEntryValid()) {
         return false;
      }
      if (!Strings.isValid(getEntry())) {
         setErrorString("Enter Link Name");
         return false;
      }
      if (!Strings.isValid(getUrl())) {
         setErrorString("Must Enter URL");
         return false;
      }
      return true;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control control = super.createButtonBar(parent);
      handleModified();
      return control;
   }

   public Link getLink() {
      return link;
   }

   public void setLink(Link link) {
      this.link = link;
   }

}
