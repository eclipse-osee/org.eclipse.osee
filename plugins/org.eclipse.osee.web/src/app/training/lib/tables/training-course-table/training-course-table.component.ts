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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import {
	MatFormField,
	MatLabel,
	MatPrefix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { HeaderService } from '@osee/shared/services';
import { Observable, map } from 'rxjs';
import { CourseInfoDialogComponent } from '../../dialogs/course-info-dialog/course-info-dialog.component';
import { TrainingCourseService } from '../../services/training-course.service';
import { TrainingCourseRecord } from '../../types/training-course';
import { trainingCourseRecordHeaderDetails } from './training-course-table-headers';

@Component({
	standalone: true,
	selector: 'osee-training-course-table',
	templateUrl: './training-course-table.component.html',
	styles: [],
	imports: [
		NgClass,
		AsyncPipe,
		MatFormField,
		MatLabel,
		MatInput,
		MatPrefix,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatButton,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
	],
})
export class TrainingCourseTableComponent {
	private dataSource = new MatTableDataSource<TrainingCourseRecord>();

	courseRecordsAsMatTableDataSource$: Observable<
		MatTableDataSource<TrainingCourseRecord>
	> = this.trainingCourseService.getTrainingCourseRecords().pipe(
		map((courses) => {
			const dataSource = this.dataSource;
			dataSource.data = courses;
			return dataSource;
		})
	);

	constructor(
		private headerService: HeaderService,
		private trainingCourseService: TrainingCourseService,
		public dialog: MatDialog
	) {}

	filterPredicate(data: TrainingCourseRecord, filter: string) {
		const filterLower = filter.toLowerCase();
		if (
			data.userName.toLowerCase().includes(filterLower) ||
			data.courseID.toLowerCase().includes(filterLower) ||
			data.startDate.toLowerCase().includes(filterLower) ||
			data.endDate.toLowerCase().includes(filterLower)
		) {
			return true;
		}
		return false;
	}

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.dataSource.filterPredicate = this.filterPredicate;
		this.dataSource.filter = filterValue;
	}

	getTableHeadersByName(header: keyof TrainingCourseRecord) {
		return this.headerService.getHeaderByName(
			trainingCourseRecordHeaderDetails,
			header
		);
	}

	recordHeaders: (keyof TrainingCourseRecord)[] = [
		'userName',
		'courseID',
		'startDate',
		'endDate',
	];

	viewCourseInfo(courseID: string) {
		this.dialog.open(CourseInfoDialogComponent, {
			minWidth: '60%',
			data: courseID,
		});
	}
}
