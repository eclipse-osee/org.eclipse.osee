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
package org.eclipse.osee.ats.api.reqts.icd;

import java.util.Set;

/**
 * Program-specific facade over SigDb. Concrete implementations are responsible for talking to the program's chosen
 * signal database (Oracle, Postgres, files, services, etc.) and mapping its schema into the generic view used by the
 * signal checker. The demo implementation serves as a reference for how other programs would implement this.
 *
 * @author Donald G. Dunne
 */
public interface SigDbApi {

   boolean isSignalDefined(String signalName, String variantId);

   SignalDefinition readSignal(String signalName, String variantId);

   Set<String> listVariantIds();

}
