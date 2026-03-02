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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XArtifactListWidget extends XListViewerWidget {

   public static WidgetId ID = WidgetId.XArtifactListWidget;
   public XArtifactListWidget() {
      this("");
   }

   public XArtifactListWidget(String displayLabel) {
      super(ID, displayLabel);
      setLabelProvider(new ArtifactLabelProvider());
      setContentProvider(new ArrayContentProvider());
   }

   public Collection<Artifact> getSelectedArtifacts() {
      return Collections.castMatching(Artifact.class, getSelected());
   }

   @Override
   public void setWidData(XWidgetData widData) {
      super.setWidData(widData);
      setMultiSelect(widData.is(XOption.MULTI_SELECT));
   }
}