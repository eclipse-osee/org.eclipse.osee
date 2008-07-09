/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.EveryoneGroup;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;

/**
 * @author Ryan D. Brooks
 */
public class ExperimentalBlam extends AbstractBlam {
   private static final String UpdateRelationModType =
         "UPDATE osee_define_rel_link SET modification_id = 3 WHERE gamma_id = ?";
   private static final String UpdateTxsCurrent =
         "UPDATE osee_define_txs SET tx_current = 2, mod_type = 3 WHERE gamma_id = ?";

   //   private static final String checkForOnBaselined = "Select * from "

   int[] gammaIds =
         new int[] {852176, 1806465, 1806464, 1806466, 1806468, 852190, 852191, 1543816, 1807037, 1806492, 1806481,
               1806485, 1713318, 177456, 1806507, 177457, 177458, 177459, 177460, 177461, 177462, 177463, 1806499,
               1806498, 1806497, 1806502, 1806501, 1806500, 1806522, 173862, 852192, 852199, 1611799, 1498880, 177449,
               1495752, 177451, 1611800, 1806513, 177453, 177455, 1806516, 177454, 1575261, 223175, 1807087, 1807086,
               1807085, 1807084, 1800592, 1164028, 1807088, 1807089, 3249936, 1575289, 1575288, 1575284, 1575281,
               1575276, 1508988, 2848359, 1575272, 2848358, 2848361, 1508983, 2848360, 2848363, 1575268, 2848362,
               1508979, 1575265, 1137539, 898640, 173020, 1806903, 174666, 1806908, 1806364, 1806904, 1806863, 1806650,
               839426, 1812929, 1806391, 1806389, 1589156, 1806393, 264571, 1806392, 1589154, 1589155, 1806400, 264757,
               1806412, 1806413, 1806414, 3198866, 1806411, 1812923, 1812922, 467177, 1806422, 1806417, 1812925,
               1812924, 1806418, 1311395, 1482583, 1311387, 1806438, 1806244, 1806439, 1806245, 1806914, 1806246,
               1806436, 1806247, 1806437, 2078493, 2078494, 1806243, 1806446, 1806252, 1806447, 1806444, 2092598,
               1806445, 1806442, 1806443, 1806249, 1806440, 1806441, 1806251, 1806454, 1806453, 1806452, 1806451,
               1953629, 1806450, 2080927, 1806449, 1806448, 1806463, 1806462, 1806461, 1806460, 1806459, 261366,
               1806458, 1806457, 1806456, 261365};

   long[] gammaIdsToDelete =
         new long[] {1806465, 1806464, 1806466, 1806468, 1543816, 1807037, 1806492, 1806481, 1806485, 1806507, 1806499,
               1806498, 1806497, 1806502, 1806501, 1806500, 1806522, 1611799, 1498880, 1495752, 1611800, 1806513,
               1806516, 1575261, 223175, 1807087, 1807086, 1807085, 1807084, 1800592, 1164028, 1807088, 1807089,
               1575289, 1575288, 1575284, 1575281, 1575276, 1508988, 1575272, 1575268, 1508979, 1575265, 1137539,
               1806903, 1806908, 1806364, 1806904, 1806650, 1806863, 1812929, 1806391, 1806389, 1589156, 1806393,
               264571, 1806392, 1589154, 1589155, 1806400, 1806412, 1806413, 1806414, 1806411, 1812923, 1812922,
               1806422, 467177, 1806417, 1812925, 1806418, 1812924, 1482583, 1311387, 1806438, 1806244, 1806439,
               1806245, 1806914, 1806436, 1806246, 1806247, 1806437, 2078493, 2078494, 1806243, 1806446, 1806252,
               1806447, 2092598, 1806444, 1806445, 1806442, 1806443, 1806249, 1806440, 1806441, 1806251, 1806454,
               1806453, 1806452, 1806451, 1806450, 1953629, 2080927, 1806449, 1806448, 1806463, 1806462, 1806461,
               1806460, 1806459, 261366, 1806458, 1806457, 261365, 1806456};

   long[] gammaIdsToDeleteTheLatest = new long[] {2848358, 898640, 173862, 264757, 1508983, 852190, 1311395, 3249936};

   long[] gammaIdsBasedOnV13Messup = new long[] {2848362, 2848363, 2848361, 2848360, 2848359};

   long[] moregammaIdsBasedOnV13Messup =
         new long[] {177461, 177456, 177453, 177454, 177449, 177451, 177462, 177457, 177458, 177458, 177463, 177460,
               177459, 177455, 852176, 839426, 852199, 852192, 852191, 173020, 174666};

   // TAsks from V13 DP Test 8694 that needed relations deleted (artifacts were already deleted)
   long[] tasksThatShouldbeDeleted =
         new long[] {2607662, 2607713, 2607644, 2607704, 2607725, 2607656, 2607626, 2607620, 2607638, 2607692, 2607614,
               2607668, 2607719, 2607650, 2607674, 2607632, 2607686, 2607680, 2607698, 2607731};

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Object[]> updateParameters = new ArrayList<Object[]>(gammaIds.length);

      //      for (long gammaId : tasksThatShouldbeDeleted) {
      //         updateParameters.add(new Object[] {SQL3DataType.BIGINT, gammaId});
      //      }

      //      StringBuilder sb 
      //      
      //ConnectionHandler.runPreparedUpdateBatch(UpdateRelationModType, updateParameters);
      //ConnectionHandler.runPreparedUpdateBatch(UpdateTxsCurrent, updateParameters);
      EveryoneGroup.getEveryoneGroup().getArtifacts(CoreRelationEnumeration.Users_User, User.class);
   }
}