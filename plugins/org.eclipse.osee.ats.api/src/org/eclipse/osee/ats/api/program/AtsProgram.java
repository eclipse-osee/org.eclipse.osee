/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.program;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.UuidNamedIdentity;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsProgram extends UuidNamedIdentity<Long> implements IAtsProgram {

   private final boolean active;

   public AtsProgram(String name, Long uuid, boolean active) {
      super(uuid, name);
      this.active = active;
   }

   @Override
   public boolean isActive() throws OseeCoreException {
      return active;
   }

}
