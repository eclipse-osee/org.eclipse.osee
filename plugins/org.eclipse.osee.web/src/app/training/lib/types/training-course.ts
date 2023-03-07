/*********************************************************************
 * Copyright (c) 2023 Boeing
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
export interface TrainingCourse {
	courseID: string;
	courseTitle: string;
	deliveryMethod: string;
	optional: boolean;
	status: boolean;
	docTitle: string;
	link: string;
}
export interface TrainingCourseRecord {
	userName: string;
	courseID: string;
	startDate: string;
	endDate: string;
}
export class DefaultTrainingCourseRecord implements TrainingCourseRecord {
	public userName = '';
	public courseID = '';
	public startDate = '';
	public endDate = '';
}
