/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.io.Serializable;
import java.util.Comparator;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionSorter implements Comparator<IAtsTeamDefinition>, Serializable {

   private static final long serialVersionUID = 1L;

   public TeamDefinitionSorter() {
      super();
   }

   @Override
   public int compare(IAtsTeamDefinition o1, IAtsTeamDefinition o2) {
      return o1.getName().compareTo(o2.getName());
   }
}