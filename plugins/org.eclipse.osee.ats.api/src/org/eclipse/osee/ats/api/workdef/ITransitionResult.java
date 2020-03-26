/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;

/**
 * @author Donald G. Dunne
 */
@JsonSerialize(as = TransitionResult.class)
@JsonDeserialize(as = TransitionResult.class)
public interface ITransitionResult {

   public String getDetails();

   public String getException();

}
