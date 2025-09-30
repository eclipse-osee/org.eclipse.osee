/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.framework.core.publishing;

import java.util.Optional;
import org.eclipse.osee.framework.core.data.ArtifactToken;

public interface PublishingOutputFormatter {

   String formatDataRightsOpen(String classification, String content);

   String formatDataRightsClose(String content);

   String formatToc(String content);

   String getDataRightsCss(String classification, String content);

   PublishingOutputFormatMode getFormatMode();

   String getFormatModeAsString();

   ArtifactToken getDataRightsMappingArtifact();

   String getCollectedCss();

   void addCss(String key, String value);

   boolean cssKeyExists(String key);

   Optional<String> getDefaultCaptionStyle();
}
