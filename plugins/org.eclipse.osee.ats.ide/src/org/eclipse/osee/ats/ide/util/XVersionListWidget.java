/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XListViewerWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XVersionListWidget extends XListViewerWidget {

   public static final WidgetId ID = WidgetIdAts.XVersionListWidget;

   public XVersionListWidget() {
      this("Versions");
   }

   public XVersionListWidget(String displayLabel) {
      super(ID, displayLabel);
      setLabelProvider(new AtsObjectLabelProvider());
      setContentProvider(new ArrayContentProvider());
   }

   public Collection<IAtsVersion> getSelectedAtsObjects() {
      return Collections.castMatching(IAtsVersion.class, getSelected());
   }

   public void setInputAtsObjects(Collection<? extends IAtsVersion> arts) {
      ArrayList<Object> objs = new ArrayList<>();
      objs.addAll(arts);
      setInput(objs);
   }

}