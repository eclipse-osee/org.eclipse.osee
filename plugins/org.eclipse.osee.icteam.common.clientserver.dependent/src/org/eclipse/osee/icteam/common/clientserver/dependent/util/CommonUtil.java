/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.clientserver.dependent.util;

import java.util.List;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;

import com.google.common.base.Function;

/*
 * This class is for userid reteival from transferable artifact
 * @author Ajay Chandrahasan
 */
public class CommonUtil {

  /**
   * This is for userid reteival from transferable artifact
   */
  public static final Function<ITransferableArtifact, String> USER_ID_RETREIVER_FROM_TRANSFERABLE_ARTIFCT =
      new Function<ITransferableArtifact, String>() {

        @Override
        public String apply(final ITransferableArtifact ar) {
          List<String> userIdL = ar.getAttributes(CoreAttributeTypes.UserId.getName());
          if ((userIdL != null) && !userIdL.isEmpty()) {
            return userIdL.get(0);
          }
          return "";
        }
      };


}
