/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.define.rest.importing.resolvers;

import org.eclipse.osee.define.rest.api.importing.RoughArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public interface IRoughArtifactTranslator {

   void translate(TransactionBuilder transaction, RoughArtifact roughArtifact, ArtifactId artifact);

}
