/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.editor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class WfeEditorContributors {

   private static List<IWfeEditorContributor> contributors = new ArrayList<>();

   public WfeEditorContributors() {
      // for osgi
   }

   public void addWfeContributor(IWfeEditorContributor contributor) {
      contributors.add(contributor);
   }

   public static List<IWfeEditorContributor> getContributors() {
      return contributors;
   }

}
