/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.xx.rel;

import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXTargetedVersionWidget extends XXAbstractXXVersionWidget {

   public static final WidgetId ID = WidgetIdAts.XXTargetedVersionWidget;
   public static final String TARGETED_VERSION = "Targeted Version";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXTargetedVersionWidget() {
      super(ID, TARGETED_VERSION, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
      setOseeImage(AtsImage.VERSION);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof ArtifactToken;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return new TargetedVersionValidator(xxWid, getLabel());
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected boolean isVersionLoaded() {
      return xxWid.isTargetedVersionLoaded();
   }

   @Override
   protected void loadVersion() {
      xxWid.getTargetedVersion();
   }

   @Override
   protected IAtsVersion getVersion() {
      return xxWid.getTargetedVersion();
   }

}
