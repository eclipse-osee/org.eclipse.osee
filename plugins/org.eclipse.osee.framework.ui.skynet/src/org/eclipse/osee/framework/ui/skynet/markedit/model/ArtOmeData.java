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
package org.eclipse.osee.framework.ui.skynet.markedit.model;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.markedit.OseeMarkdownEditorInput;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

/**
 * @author Donald G. Dunne
 */
public class ArtOmeData extends AbstractOmeData implements IArtifactEventListener, IArtifactTopicEventListener {

   OseeMarkdownEditorInput editorInput;
   private XTextDam editXText;

   public ArtOmeData(OseeMarkdownEditorInput editorInput) {
      this.editorInput = editorInput;
   }

   @Override
   public String getEditorName() {
      return String.format("OME: %s", getArtifact().toStringWithId());
   }

   public Artifact getArtifact() {
      return editorInput.getArtifact();
   }

   @Override
   public boolean isDirty() {
      return getArtifact().isDirty();
   }

   @Override
   public void setWidget(XText editText) {
      this.editXText = (XTextDam) editText;
   }

   @Override
   public void doSave() {
      Artifact artifact = getArtifact();
      String content = editXText.get();
      if (!Strings.isValid(content)) {
         content = "";
      }
      artifact.setSoleAttributeValue(CoreAttributeTypes.MarkdownContent, content);
      artifact.persist(String.format("%s - %s", getClass().getSimpleName(), artifact.toStringWithId()));
   }

   @Override
   public void onSaveException(OseeCoreException ex) {
      XResultData rd = AccessControlArtifactUtil.getXResultAccessHeader("Artifact Editor - Save", getArtifact());
      rd.logf("\n\n%s", Lib.exceptionToString(ex));
      XResultDataUI.report(rd, "OME Save Editor");
   }

   @Override
   public void dispose() {
      // If the artifact is dirty when the editor gets disposed, then it needs to be reverted
      Artifact artifact = getArtifact();
      if (artifact != null && !artifact.isDeleted() && artifact.isDirty()) {
         try {
            artifact.reloadAttributesAndRelations();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void load() {
      if (editXText != null) {
         editXText.setAttributeType(getArtifact(), CoreAttributeTypes.MarkdownContent);
         String value = editXText.get();
         setMdContent(value);
      } else {
         setMdContent(getArtifact().getSoleAttributeValue(CoreAttributeTypes.MarkdownContent, ""));
      }
   }

   @Override
   public XText createXText(boolean enabled) {
      editXText = new XTextDam(enabled ? "Enter Markdown" : "Markdown (Read-Only)");
      return editXText;
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactEvent, Sender sender) {
      if (sender.isRemote() && artifactEvent.isHasEvent(getArtifact())) {
         load();
      }
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (sender.isRemote() && artifactEvent.isHasEvent(getArtifact())) {
         load();
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Collections.emptyList();
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return Collections.emptyList();
   }

   @Override
   public void uponCreate(XText editText) {
      ArtOmeData fOmeData = this;
      editText.getStyledText().addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (Widgets.isAccessible(editText.getStyledText())) {
               OseeEventManager.removeListener(fOmeData);
            }
         }
      });
      OseeEventManager.addListener(fOmeData);
   }

   @Override
   public boolean isEditable() {
      if (editable == null) {
         editable = !ServiceUtil.accessControlService().isReadOnly(getArtifact());
      }
      return editable;
   }

}
