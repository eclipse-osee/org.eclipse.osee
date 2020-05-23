/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.disposition.rest.internal.report;

import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.rest.util.DispoUtil;

/**
 * @author Megumi Telles
 */
public class FindReruns {

   public HashMap<String, String> createList(List<DispoAnnotationData> annotations) {
      return DispoUtil.splitTestScriptNameAndPath(annotations);
   }
}
