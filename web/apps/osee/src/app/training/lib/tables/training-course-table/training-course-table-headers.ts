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
import { headerDetail } from '@osee/shared/types';
import {
	TrainingCourse,
	TrainingCourseRecord,
} from '../../types/training-course';

export const trainingCourseRecordHeaderDetails: headerDetail<TrainingCourseRecord>[] =
	[
		{
			header: 'userName',
			description: 'User Name',
			humanReadable: 'User Name',
		},
		{
			header: 'courseID',
			description: 'Course ID',
			humanReadable: 'Course ID',
		},
		{
			header: 'startDate',
			description: 'Course Start Date',
			humanReadable: 'Start Date',
		},
		{
			header: 'endDate',
			description: 'Course End Date',
			humanReadable: 'End Date',
		},
	];
