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
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { HeaderService } from '@osee/shared/services';
import { Component } from '@angular/core';
import { TrainingCourseService } from '../../services/training-course.service';
import { TrainingCourseRecord } from '../../types/training-course';
import { trainingCourseRecordHeaderDetails } from './training-course-table-headers';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { map, Observable } from 'rxjs';
import { MatInputModule } from '@angular/material/input';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CourseInfoDialogComponent } from './../../dialogs/course-info-dialog/course-info-dialog.component';

@Component({
	standalone: true,
	selector: 'osee-training-course-table',
	templateUrl: './training-course-table.component.html',
	styles: [],
	imports: [
		MatFormFieldModule,
		MatTableModule,
		CommonModule,
		MatIconModule,
		MatInputModule,
		MatButtonModule,
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
