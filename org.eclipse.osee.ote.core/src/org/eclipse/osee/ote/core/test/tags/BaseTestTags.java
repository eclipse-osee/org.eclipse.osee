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
package org.eclipse.osee.ote.core.test.tags;

/**
 * @author Roberto E. Escobar
 */
public abstract interface BaseTestTags {
   public static final String TEST_SCRIPT = "TestScript";
   
   public static final String NAME_FIELD = "Name";
   public static final String NUMBER_FIELD = "Number";
   public static final String LOCATION_FIELD = "Location";
   public static final String SCRIPT_SOURCE_FIELD = "ScriptSource";
   public static final String SCRIPT_LINE_FIELD = "ScriptLine";   
   public static final String FAILURE_TYPE = "FailureType";

   public static final String DESCRIPTION_FIELD = "Description";
   public static final String MESSAGE_FIELD = "Message";
   public static final String DETAILS_FIELD = "Details";
   public static final String ITEMS_FIELD = "Item";
   public static final String TIME_FIELD = "Time";
   
   public static final String REQUIREMENT_ID_FIELD = "RequirementId";
   
   public static final String PURPOSE_FIELD = "Purpose";
   public static final String PRECONDITION_FIELD = "PreCondition";
   public static final String POSTCONDITION_FIELD = "PostCondition";
   
   public static final String CONFIG_ENTRY = "Config";
   public static final String SCRIPT_NAME = "ScriptName";
   public static final String SCRIPT_VERSION = "ScriptVersion";
   public static final String EXECUTED_BY = "ExecutedBy";
   public static final String EXECUTION_DATE = "ExecutionDate";
   public static final String ENVIRONMENT_FIELD = "Environment";
   public static final String CLEAR_TOOL_VIEW = "ClearToolView";
   
   public static final String NATIVE_ENVIRONMENT = "NativeEnvironment";
   public static final String ENVIRONMENT_VARIABLE = "EnvironmentVariable";
   public static final String OSEE_ENVIRONMENT_PREFIX = "OSEEKEY_";
   public static final String PATH_FIELD = "Path";   
   
   public static final String SCRIPT_TIME_FIELD = "ScriptResult";
   public static final String ELAPSED_TIME_FIELD = "ElapsedTime";

   public static final String EXECUTION_STATUS = "ExecutionStatus";
   public static final String EXECUTION_RESULT = "ExecutionResult";   
   public static final String EXECUTION_SUCCESS = "SUCCESS";
   public static final String EXECUTION_ABORTED = "ABORTED";
   public static final String EXECUTION_DETAILS = "ExecutionDetails";
   public static final String TARGET_ENVIRONMENT = "TargetEnvironment";   
   
   public static final String SECTION_BREAK = "----------------------------------------------------------------";

   public static final String BEMS_FIELD = "BEMS";
   public static final String EMAIL_FIELD = "Email";

   public static final String REPOSITORY_TYPE = "repositoryType";

   public static final String MODIFIED_FIELD = "modifiedFlag";
   public static final String REVISION_FIELD = "revision";
   public static final String LAST_AUTHOR_FIELD = "lastAuthor";
   public static final String LAST_MODIFIED = "lastModified";
   public static final String URL = "url";

   public static final String UNDEFINED_ENVIRONMENT = "Undefined";

public static final String Traceability = "Traceability";
}
