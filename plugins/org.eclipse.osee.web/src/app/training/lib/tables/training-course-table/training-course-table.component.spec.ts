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
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrainingCourseTableComponent } from './training-course-table.component';
import { TrainingCourseService } from '../../services/training-course.service';
import { TrainingCourseServiceMock } from '../../testing/training-course.service.mock';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';

describe('TrainingCourseTableComponent', () => {
	let component: TrainingCourseTableComponent;
	let fixture: ComponentFixture<TrainingCourseTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatTableModule,
				MatFormFieldModule,
				MatInputModule,
				BrowserAnimationsModule,
				MatIconModule,
				TrainingCourseTableComponent,
				MatDialogModule,
			],
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

		fixture = TestBed.createComponent(TrainingCourseTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
