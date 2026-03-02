/*********************************************************************
 * Copyright (c) 2012 Boeing
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

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.osgi.service.component.annotations.Component;

/**
 * Branch Selection with branch uuid storage as String
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XBranchSelectArtWidget extends XBranchSelectWidget {

   public static final WidgetId ID = WidgetId.XBranchSelectArtWidget;

   public XBranchSelectArtWidget() {
      super(ID, "Branch");
   }

   public XBranchSelectArtWidget(String label) {
      super(label);
   }

   public Long getStoredUuid() {
      long resultUuid = 0;
      String uuidStr = getArtifact().getSoleAttributeValue(getAttributeType(), null);
      if (Strings.isValid(uuidStr)) {
         resultUuid = Long.valueOf(uuidStr);
      }
      return resultUuid;
   }

   @Override
   public void refresh() {
      setLabel(getAttributeType().getUnqualifiedName());
      Long storedUuid = getStoredUuid();
      if (storedUuid != null && storedUuid > 0) {
         setSelection(BranchManager.getBranchToken(storedUuid));
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      refresh();
   }

}
