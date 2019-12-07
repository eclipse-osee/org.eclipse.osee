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
package org.eclipse.osee.ote.define;

import static org.eclipse.osee.ote.define.OteTypeTokenProvider.ote;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

public interface OteAttributeTypes {

   // @formatter:off
   AttributeTypeString BuildId = ote.createString(1152921504606847306L, "Build Id", MediaType.TEXT_PLAIN, "Build Id");
   AttributeTypeString Checksum = ote.createString(1152921504606847307L, "Checksum", MediaType.TEXT_PLAIN, "Test Case Checksum");
   AttributeTypeString ElapsedDate = ote.createString(1152921504606847296L, "Elapsed Date", MediaType.TEXT_PLAIN, "Time Elapsed from the start to the end of the script");
   AttributeTypeDate EndDate = ote.createDate(1152921504606847295L, "End Date", AttributeTypeToken.TEXT_CALENDAR, "Stop Date");
   AttributeTypeInteger Failed = ote.createInteger(1152921504606847298L, "Failed", MediaType.TEXT_PLAIN, "Number of test points that failed");
   AttributeTypeBoolean IsBatchModeAllowed = ote.createBoolean(1152921504606847303L, "Is Batch Mode Allowed", MediaType.TEXT_PLAIN, "Whether Test Script is allowed to run as part of a batch");
   AttributeTypeString LastAuthor = ote.createString(1152921504606847285L, "Last Author", MediaType.TEXT_PLAIN, "Last Author");
   AttributeTypeDate LastDateUploaded = ote.createDate(1152921504606847304L, "Last Date Uploaded", AttributeTypeToken.TEXT_CALENDAR, "Last time outfile was uploaded");
   AttributeTypeDate LastModifiedDate = ote.createDate(1152921504606847286L, "Last Modified Date", AttributeTypeToken.TEXT_CALENDAR, "Last Modified");
   AttributeTypeString ModifiedFlag = ote.createString(1152921504606847284L, "Modified Flag", MediaType.TEXT_PLAIN, "File Modification Flag from Repository");
   AttributeTypeString OsArchitecture = ote.createString(1152921504606847287L, "OS Architecture", MediaType.TEXT_PLAIN, "OS Architecture");
   AttributeTypeString OsName = ote.createString(1152921504606847288L, "OS Name", MediaType.TEXT_PLAIN, "OS Name");
   AttributeTypeString OsVersion = ote.createString(1152921504606847289L, "OS Version", MediaType.TEXT_PLAIN, "OS Version");
   AttributeTypeString OseeServerJarVersion = ote.createString(1152921504606847292L, "OSEE Server Jar Version", MediaType.TEXT_PLAIN, "OSEE Server Jar Version");
   AttributeTypeString OseeServerTitle = ote.createString(1152921504606847291L, "OSEE Server Title", MediaType.TEXT_PLAIN, "OSEE Server Title");
   AttributeTypeString OseeVersion = ote.createString(1152921504606847290L, "OSEE Version", MediaType.TEXT_PLAIN, "OSEE Version");
   AttributeTypeString OutfileUrl = ote.createString(1152921504606847281L, "Outfile URL", MediaType.TEXT_PLAIN, "Test Run Content");
   AttributeTypeInteger Passed = ote.createInteger(1152921504606847297L, "Passed", MediaType.TEXT_PLAIN, "Number of test points that passed");
   AttributeTypeString ProcessorId = ote.createString(1152921504606847293L, "Processor ID", MediaType.TEXT_PLAIN, "Processor ID");
   AttributeTypeString QualificationLevel = ote.createString(1152921504606847305L, "Qualification Level", MediaType.TEXT_PLAIN, "Qualification level");
   AttributeTypeBoolean RanInBatchMode = ote.createBoolean(1152921504606847302L, "Ran In Batch Mode", MediaType.TEXT_PLAIN, "Run was performed as part of a batch");
   AttributeTypeString Revision = ote.createString(1152921504606847283L, "Revision", MediaType.TEXT_PLAIN, "Version");
   AttributeTypeBoolean ScriptAborted = ote.createBoolean(1152921504606847300L, "Script Aborted", MediaType.TEXT_PLAIN, "Test Abort status");
   AttributeTypeDate StartDate = ote.createDate(1152921504606847294L, "Start Date", AttributeTypeToken.TEXT_CALENDAR, "Start Date");
   AttributeTypeString TestDisposition = ote.createString(1152921504606847308L, "Test Disposition", MediaType.TEXT_PLAIN, "Disposition");
   AttributeTypeString TestScriptUrl = ote.createString(1152921504606847282L, "Test Script URL", MediaType.TEXT_PLAIN, "Url of the test script used");
   AttributeTypeInteger TotalTestPoints = ote.createInteger(1152921504606847299L, "Total Test Points", MediaType.TEXT_PLAIN, "Total test points");
   // @formatter:on

}