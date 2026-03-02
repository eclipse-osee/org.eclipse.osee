/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.osgi.service.component.annotations.Component;

/**
 * Widget providing attribute label, select button with filterable list and readonly name of selected artifact and saves
 * to getArtifact().
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XArtifactSelectPersistWidget extends XArtifactSelectWidget {

   public static WidgetId ID = WidgetId.XArtifactSelectPersistWidget;

   public XArtifactSelectPersistWidget() {
      this("");
   }

   public XArtifactSelectPersistWidget(String label) {
      this(ID, label);
   }

   public XArtifactSelectPersistWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
   }

   public Artifact getStored() {
      Object obj = getArtifact().getSoleAttributeValue(getAttributeType(), null);
      if (obj instanceof Integer) {
         return ArtifactQuery.getArtifactFromId((Integer) obj, getArtifact().getBranch());
      } else if (obj instanceof Artifact) {
         return (Artifact) obj;
      }
      return null;
   }

   @Override
   public void refresh() {
      Artifact storedArt = getStored();
      if (storedArt != null) {
         setSelection(storedArt);
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      refresh();
   }

   @Override
   public Collection<Artifact> getSelectableArtifacts() {
      return java.util.Collections.emptyList();
   }

}
