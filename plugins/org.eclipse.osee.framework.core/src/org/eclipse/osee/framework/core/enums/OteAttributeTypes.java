/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.ote;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;

public interface OteAttributeTypes {

   // @formatter:off
   AttributeTypeString OutfileUrl = ote.createString(1152921504606847281L, "Outfile URL", MediaType.TEXT_PLAIN, "Test Run Content", "tmo");
   AttributeTypeString BuildId = ote.createString(1152921504606847306L, "Build Id", MediaType.TEXT_PLAIN, "Build Id");
   AttributeTypeString Checksum = ote.createString(1152921504606847307L, "Checksum", MediaType.TEXT_PLAIN, "Test Case Checksum");
   AttributeTypeBoolean IsBatchModeAllowed = ote.createBoolean(1152921504606847303L, "Is Batch Mode Allowed", MediaType.TEXT_PLAIN, "Whether Test Script is allowed to run as part of a batch");
   AttributeTypeDate LastDateUploaded = ote.createDate(1152921504606847304L, "Last Date Uploaded", AttributeTypeToken.TEXT_CALENDAR, "Last time outfile was uploaded");
   AttributeTypeBoolean RanInBatchMode = ote.createBoolean(1152921504606847302L, "Ran In Batch Mode", MediaType.TEXT_PLAIN, "Run was performed as part of a batch");
   AttributeTypeString TestDisposition = ote.createString(1152921504606847308L, "Test Disposition", MediaType.TEXT_PLAIN, "Disposition", DisplayHint.SingleLine);
   AttributeTypeString TestScriptUrl = ote.createString(1152921504606847282L, "Test Script URL", MediaType.TEXT_PLAIN, "Url of the test script used");
   // @formatter:on

}