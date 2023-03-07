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
import { of } from 'rxjs';
import { TrainingCourseService } from '../services/training-course.service';
import { TrainingCourse, TrainingCourseRecord } from '../types/training-course';

export const TrainingCourseServiceMock: Partial<TrainingCourseService> = {
	getTrainingCourseRecords() {
		return of(TrainingCourseRecordsMock);
	},
	getTrainingCourses() {
		return of(TrainingCoursesMock);
	},
};

export const TrainingCourseRecordsMock: TrainingCourseRecord[] = [
	{
		userName: 'John Smith',
		courseID: '12345',
		startDate: '1/2/2023',
		endDate: '4/27/2023',
	},
	{
		userName: 'John Smith',
		courseID: '12346',
		startDate: '1/2/2023',
		endDate: '4/27/2023',
	},
	{
		userName: 'John Smith',
		courseID: '12347',
		startDate: '1/2/2023',
		endDate: '4/27/2023',
	},
];

export const TrainingCoursesMock: TrainingCourse[] = [
	{
		courseID: '12345',
		courseTitle: 'Tester Training',
		deliveryMethod: 'web',
		optional: false,
		status: true,
		docTitle: 'Tester Training 123',
		link: 'https://www.google.com/',
	},
	{
		courseID: '12346',
		courseTitle: 'Tester Ethics',
		deliveryMethod: 'video',
		optional: false,
		status: false,
		docTitle: 'Tester Ethics 123',
		link: 'https://www.google.com/',
	},
	{
		courseID: '12347',
		courseTitle: 'Tester Training - Advanced',
		deliveryMethod: 'class',
		optional: false,
		status: false,
		docTitle: 'Tester Advanced 123',
		link: 'https://www.google.com/',
	},
];
