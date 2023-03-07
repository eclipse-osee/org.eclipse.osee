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
import { MatTableModule } from '@angular/material/table';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrainingRoleTableComponent } from './training-role-table.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TrainingRoleService } from '../../services/training-role.service';
import { TrainingRoleServiceMock } from '../../testing/training-role.service.mock';

describe('TrainingRoleTableComponent', () => {
	let component: TrainingRoleTableComponent;
	let fixture: ComponentFixture<TrainingRoleTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MatTableModule, TrainingRoleTableComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {},
				},
				{
					provide: TrainingRoleService,
					useValue: TrainingRoleServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(TrainingRoleTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
