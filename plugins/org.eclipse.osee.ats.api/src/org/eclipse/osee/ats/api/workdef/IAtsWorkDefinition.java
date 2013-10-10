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
package org.eclipse.osee.ats.api.workdef;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinition extends Identifiable<String>, HasDescription {

   /**
    * Identification
    */
   @Override
   public abstract String getName();

   public abstract String getId();

   @Override
   public abstract String getDescription();

   /**
    * States
    */
   public abstract List<IAtsStateDefinition> getStates();

   public abstract IAtsStateDefinition getStateByName(String name);

   public abstract IAtsStateDefinition getStartState();

}