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

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.StateDefBuilder;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;

/**
 * @author Donald G. Dunne
 */
public class WorkDefReviewPeerToPeerDemo extends WorkDefReviewPeerToPeer {

   public WorkDefReviewPeerToPeerDemo() {
      super(AtsWorkDefinitionTokens.WorkDef_Review_PeerToPeer_Demo);
   }

   @Override
   public WorkDefinition build() {
      super.build();
      WorkDefBuilder bld = getWorkDefBuilder();

      StateDefBuilder stateBld = bld.getStateDefBuilder(StateToken.Prepare);
      stateBld.insertLayoutAfter(AtsAttributeTypes.Description, //
         new CompositeLayoutItem(5, //
            new WidgetDefinition("Select Peer Review Checklist to Attach", "XAttachmentExampleWidget")) //
      );

      return bld.getWorkDefinition();
   }
}
