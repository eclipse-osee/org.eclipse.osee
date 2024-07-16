/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

/**
 * @author Donald G. Dunne
 */
public enum ReviewBlockType {
   None,
   Transition,
   Commit;

   public boolean isCommit() {
      return this.name().equals(Commit.name());
   }
};
