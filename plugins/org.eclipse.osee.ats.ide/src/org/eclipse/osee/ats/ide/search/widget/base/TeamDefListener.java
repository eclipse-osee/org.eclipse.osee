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
package org.eclipse.osee.ats.ide.search.widget.base;

/**
 * @author Donald G. Dunne
 */
public interface TeamDefListener {

   // Notification of team defs for ais updated and can be retrieved at searchItem.getTeamDefs
   public void updateAisOrTeamDefs();

}
