/**
 * Provides the classes necessary to create tasks for different cases.
 * <p>
 *
 * @link ChangeReportTaskData - Top level pojo that is sent into the ATS endpoint for task from change reports. As
 * creation operation moves through its processing, different values are added and set for future use. It contains a
 * single XResultData that all processing will use to log what was done and any errors.<br/>
 * <br/>
 * contains 1..n of <br/>
 * <br/>
 * @link ChangeReportTaskTeamWfData - PoJo that represents that work to be done for one Team Workflow and its generated
 * tasks. There will be one of these for every sibling team workflow configured for tasks.
 * <p>
 * @link ChangeReportTaskMatch - PoJo that represents 3 things. 1) ChangeReportArt it references 2) Task Title 3)
 * IAtsTask that exists or is null. One object will be created for each art/title pair. This allows for a single change
 * art to be tied to multiple tasks. eg: Different partitions need a task created for the same change art.
 */
package org.eclipse.osee.ats.api.task.create;