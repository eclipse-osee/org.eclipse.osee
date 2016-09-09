/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.query;

import java.util.Arrays;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchUtil {

   public static final String ATS_QUERY_NAMESPACE = "ats.search";
   public static final String ATS_QUERY_GOAL_NAMESPACE = "ats.search.goal";
   public static final String ATS_QUERY_TEAM_WF_NAMESPACE = "ats.search.team";
   public static final String ATS_QUERY_EV_NAMESPACE = "ats.search.ev";
   public static final String ATS_QUERY_TASK_NAMESPACE = "ats.search.task";
   public static final String ATS_QUERY_REVIEW_NAMESPACE = "ats.search.review";
   public static final List<String> ATS_DEFAULT_SEARCH_NAMESPACES =
      Arrays.asList(ATS_QUERY_NAMESPACE, ATS_QUERY_TEAM_WF_NAMESPACE, ATS_QUERY_GOAL_NAMESPACE,
         ATS_QUERY_TASK_NAMESPACE, ATS_QUERY_REVIEW_NAMESPACE, ATS_QUERY_EV_NAMESPACE);

   private AtsSearchUtil() {
      // Utilility Class
   }

}
