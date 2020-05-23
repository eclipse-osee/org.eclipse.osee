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

package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jonathan E. Jensen
 */
public interface DslGrammarStorageAdapter {

   /**
    * This function processes the data from the editor prior to it being stored in the data store. This works in
    * conjunction with preProcess() to allow the presented text to be independent of the data stored.
    * 
    * @param serializedModel
    * @return
    */
   String postProcess(Artifact artifact, String serializedModel);

   /**
    * This function processes the data from the data store prior to it being viewed by the user in the editor. This
    * works in conjunction with postProcess() to allow the presented text to be independent of the data stored.
    * 
    * @param artifact
    * @param storedValue
    * @return a valid text for consumption by the editor.
    */
   String preProcess(Artifact artifact, String storedValue);

}
