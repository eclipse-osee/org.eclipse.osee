/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef.internal.workdefs;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXAttachmentDemoWidget;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.StateDefBuilder;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.core.demo.DemoUtil;

/**
 * @author Donald G. Dunne
 */
@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefReviewPeerToPeerDemo extends WorkDefReviewPeerToPeer {

   public WorkDefReviewPeerToPeerDemo() {
      super(AtsWorkDefinitionTokens.WorkDefReviewPeerToPeerDemo);
   }

   @Override
   public WorkDefinition build() {
      super.build();
      WorkDefBuilder bld = getWorkDefBuilder();

      StateDefBuilder stateBld = bld.getStateDefBuilder(StateToken.Prepare);

      if (stateBld != null) {
         stateBld.insertLayoutAfter(Description, //
            new WidgetDef("Select Peer Review Checklist to Attach", XXAttachmentDemoWidget) //
         );
      }

      return bld.getWorkDefinition();
   }

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
