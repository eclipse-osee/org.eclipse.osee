/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * Select and persist with clear button
 *
 * @author Donald G. Dunne
 */
public class XIntroducedInVersionWithPersistWidget extends XHyperlabelVersionSelectionWithPersist {

   public static RelationTypeSide INTRODUCED_VERSION_RELATION =
      AtsRelationTypes.TeamWorkflowToIntroducedInVersion_Version;

   public XIntroducedInVersionWithPersistWidget() {
      this("Introduced In Version");
   }

   public XIntroducedInVersionWithPersistWidget(String label) {
      super(label, INTRODUCED_VERSION_RELATION);
   }

}
