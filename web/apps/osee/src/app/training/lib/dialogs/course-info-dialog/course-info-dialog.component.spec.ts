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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TrainingCourseService } from '../../services/training-course.service';
import { TrainingCourseServiceMock } from '../../testing/training-course.service.mock';
import { CourseInfoDialogComponent } from './course-info-dialog.component';

describe('CourseInfoDialogComponent', () => {
	let component: CourseInfoDialogComponent;
	let fixture: ComponentFixture<CourseInfoDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CourseInfoDialogComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {},
				},
				{
					provide: TrainingCourseService,
					useValue: TrainingCourseServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CourseInfoDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
