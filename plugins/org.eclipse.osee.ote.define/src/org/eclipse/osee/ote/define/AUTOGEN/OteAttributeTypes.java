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
package org.eclipse.osee.ote.define.AUTOGEN;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

public final class OteAttributeTypes implements OrcsTypeTokenProvider {
   private static final OrcsTypeTokens tokens = new OrcsTypeTokens();

   // @formatter:off
   public static final NamespaceToken OTE = NamespaceToken.valueOf(11, "ote", "Namespace for ote system and content management types");

   public static final AttributeTypeString BuildId = tokens.add(AttributeTypeToken.createString(1152921504606847306L, OTE, "Build Id", MediaType.TEXT_PLAIN, "Build Id"));
   public static final AttributeTypeString Checksum = tokens.add(AttributeTypeToken.createString(1152921504606847307L, OTE, "Checksum", MediaType.TEXT_PLAIN, "Test Case Checksum"));
   public static final AttributeTypeString ElapsedDate = tokens.add(AttributeTypeToken.createString(1152921504606847296L, OTE, "Elapsed Date", MediaType.TEXT_PLAIN, "Time Elapsed from the start to the end of the script"));
   public static final AttributeTypeDate EndDate = tokens.add(AttributeTypeToken.createDate(1152921504606847295L, OTE, "End Date", AttributeTypeToken.TEXT_CALENDAR, "Stop Date"));
   public static final AttributeTypeInteger Failed = tokens.add(AttributeTypeToken.createInteger(1152921504606847298L, OTE, "Failed", MediaType.TEXT_PLAIN, "Number of test points that failed"));
   public static final AttributeTypeBoolean IsBatchModeAllowed = tokens.add(AttributeTypeToken.createBoolean(1152921504606847303L, OTE, "Is Batch Mode Allowed", MediaType.TEXT_PLAIN, "Whether Test Script is allowed to run as part of a batch"));
   public static final AttributeTypeString LastAuthor = tokens.add(AttributeTypeToken.createString(1152921504606847285L, OTE, "Last Author", MediaType.TEXT_PLAIN, "Last Author"));
   public static final AttributeTypeDate LastDateUploaded = tokens.add(AttributeTypeToken.createDate(1152921504606847304L, OTE, "Last Date Uploaded", AttributeTypeToken.TEXT_CALENDAR, "Last time outfile was uploaded"));
   public static final AttributeTypeDate LastModifiedDate = tokens.add(AttributeTypeToken.createDate(1152921504606847286L, OTE, "Last Modified Date", AttributeTypeToken.TEXT_CALENDAR, "Last Modified"));
   public static final AttributeTypeString ModifiedFlag = tokens.add(AttributeTypeToken.createString(1152921504606847284L, OTE, "Modified Flag", MediaType.TEXT_PLAIN, "File Modification Flag from Repository"));
   public static final AttributeTypeString OsArchitecture = tokens.add(AttributeTypeToken.createString(1152921504606847287L, OTE, "OS Architecture", MediaType.TEXT_PLAIN, "OS Architecture"));
   public static final AttributeTypeString OsName = tokens.add(AttributeTypeToken.createString(1152921504606847288L, OTE, "OS Name", MediaType.TEXT_PLAIN, "OS Name"));
   public static final AttributeTypeString OsVersion = tokens.add(AttributeTypeToken.createString(1152921504606847289L, OTE, "OS Version", MediaType.TEXT_PLAIN, "OS Version"));
   public static final AttributeTypeString OseeServerJarVersion = tokens.add(AttributeTypeToken.createString(1152921504606847292L, OTE, "OSEE Server Jar Version", MediaType.TEXT_PLAIN, "OSEE Server Jar Version"));
   public static final AttributeTypeString OseeServerTitle = tokens.add(AttributeTypeToken.createString(1152921504606847291L, OTE, "OSEE Server Title", MediaType.TEXT_PLAIN, "OSEE Server Title"));
   public static final AttributeTypeString OseeVersion = tokens.add(AttributeTypeToken.createString(1152921504606847290L, OTE, "OSEE Version", MediaType.TEXT_PLAIN, "OSEE Version"));
   public static final AttributeTypeString OutfileUrl = tokens.add(AttributeTypeToken.createString(1152921504606847281L, OTE, "Outfile URL", MediaType.TEXT_PLAIN, "Test Run Content"));
   public static final AttributeTypeInteger Passed = tokens.add(AttributeTypeToken.createInteger(1152921504606847297L, OTE, "Passed", MediaType.TEXT_PLAIN, "Number of test points that passed"));
   public static final AttributeTypeString ProcessorId = tokens.add(AttributeTypeToken.createString(1152921504606847293L, OTE, "Processor ID", MediaType.TEXT_PLAIN, "Processor ID"));
   public static final AttributeTypeString QualificationLevel = tokens.add(AttributeTypeToken.createString(1152921504606847305L, OTE, "Qualification Level", MediaType.TEXT_PLAIN, "Qualification level"));
   public static final AttributeTypeBoolean RanInBatchMode = tokens.add(AttributeTypeToken.createBoolean(1152921504606847302L, OTE, "Ran In Batch Mode", MediaType.TEXT_PLAIN, "Run was performed as part of a batch"));
   public static final AttributeTypeString Revision = tokens.add(AttributeTypeToken.createString(1152921504606847283L, OTE, "Revision", MediaType.TEXT_PLAIN, "Version"));
   public static final AttributeTypeBoolean ScriptAborted = tokens.add(AttributeTypeToken.createBoolean(1152921504606847300L, OTE, "Script Aborted", MediaType.TEXT_PLAIN, "Test Abort status"));
   public static final AttributeTypeDate StartDate = tokens.add(AttributeTypeToken.createDate(1152921504606847294L, OTE, "Start Date", AttributeTypeToken.TEXT_CALENDAR, "Start Date"));
   public static final AttributeTypeString TestDisposition = tokens.add(AttributeTypeToken.createString(1152921504606847308L, OTE, "Test Disposition", MediaType.TEXT_PLAIN, "Disposition"));
   public static final AttributeTypeString TestScriptUrl = tokens.add(AttributeTypeToken.createString(1152921504606847282L, OTE, "Test Script URL", MediaType.TEXT_PLAIN, "Url of the test script used"));
   public static final AttributeTypeInteger TotalTestPoints = tokens.add(AttributeTypeToken.createInteger(1152921504606847299L, OTE, "Total Test Points", MediaType.TEXT_PLAIN, "Total test points"));
   // @formatter:on

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokens.registerTypes(tokenService);
   }
}