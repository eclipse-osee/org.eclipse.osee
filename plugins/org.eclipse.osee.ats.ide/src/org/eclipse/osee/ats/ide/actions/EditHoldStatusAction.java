/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Bhawana Mishra
 */
public class EditHoldStatusAction extends AbstractEditSubStatusAction {

   public EditHoldStatusAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super(selectedAtsArtifacts, "Hold", "UnHold", FrameworkImage.HOLD, AtsAttributeTypes.HoldReason);
   }

}
