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

package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;

/**
 * Actionable Item Combo showing all (active and inactive) AIs
 *
 * @author Donald G. Dunne
 */
public class XActionableItemAllCombo extends XActionableItemCombo {

   public XActionableItemAllCombo() {
      super(Active.Both);
      setLabelProvider(new AtsObjectLabelProvider(true));
   }

}
