/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.ide.column.RelatedToStateColumnUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWithFilteredDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForRelatedState extends XHyperlinkWithFilteredDialog<String> {

   private final Collection<String> selectable;

   public XHyperlinkWfdForRelatedState(Collection<String> selectable) {
      super("Related State");
      this.selectable = selectable;
      setToolTip(RelatedToStateColumnUI.RELATED_TO_STATE_SELECTION);
   }

   @Override
   public Collection<String> getSelectable() {
      if (selectable != null) {
         return selectable;
      } else {
         return Collections.emptyList();
      }
   }

}
