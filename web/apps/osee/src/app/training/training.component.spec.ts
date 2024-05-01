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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { AddCoursesDialogComponent } from './lib/dialogs/add-courses-dialog/add-courses-dialog.component';
import { AddRolesDialogComponent } from './lib/dialogs/add-roles-dialog/add-roles-dialog.component';
import { EditCoursesDropdownComponent } from './lib/dropdowns/edit-courses-dropdown/edit-courses-dropdown.component';
import { EditRolesDropdownComponent } from './lib/dropdowns/edit-roles-dropdown/edit-roles-dropdown.component';
import { TrainingCourseTableComponent } from './lib/tables/training-course-table/training-course-table.component';
import { TrainingRoleTableComponent } from './lib/tables/training-role-table/training-role-table.component';
import { TrainingComponent } from './training.component';
import { TrainingRoleService } from './lib/services/training-role.service';
import { TrainingRoleServiceMock } from './lib/testing/training-role.service.mock';
import { TrainingCourseService } from './lib/services/training-course.service';
import { TrainingCourseServiceMock } from './lib/testing/training-course.service.mock';

describe('TrainingComponent', () => {
	let component: TrainingComponent;
	let fixture: ComponentFixture<TrainingComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatMenuModule,
				MatIconModule,
				MatTableModule,
				MatFormFieldModule,
				MatInputModule,
				BrowserAnimationsModule,
				TrainingComponent,
				EditCoursesDropdownComponent,
				EditRolesDropdownComponent,
				TrainingRoleTableComponent,
				TrainingCourseTableComponent,
				AddRolesDialogComponent,
				AddCoursesDialogComponent,
			],
			providers: [
				{ provide: MatDialog, useValue: {} },
				{
					provide: TrainingRoleService,
					useValue: TrainingRoleServiceMock,
				},
				{
					provide: TrainingCourseService,
					useValue: TrainingCourseServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(TrainingComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
