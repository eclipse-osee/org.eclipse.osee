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

package org.eclipse.osee.ats.api.util;

import java.util.Collection;
import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public interface IValueProvider {

   public String getName();

   public boolean isEmpty();

   public Collection<String> getValues();

   public Collection<Date> getDateValues();

}
