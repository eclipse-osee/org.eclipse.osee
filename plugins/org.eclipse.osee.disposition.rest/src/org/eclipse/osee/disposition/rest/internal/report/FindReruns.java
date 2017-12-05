/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
