/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.util;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;

public class AtsUtilService implements IAtsUtilService {

   private final IAttributeResolver attrResolver;

   public AtsUtilService(IAttributeResolver attrResolver) {
      this.attrResolver = attrResolver;
   }

   @Override
   public void setAtsId(ISequenceProvider sequenceProvider, IAtsObject newObject, IAtsTeamDefinition teamDef, IAtsChangeSet changes) {
      new AtsIdProvider(sequenceProvider, attrResolver, newObject, teamDef).setAtsId(changes);
   }

   @Override
   public String getNextAtsId(ISequenceProvider sequenceProvider, IAtsObject newObject, IAtsTeamDefinition teamDef) {
      return new AtsIdProvider(sequenceProvider, attrResolver, newObject, teamDef).getNextAtsId();
   }

}
