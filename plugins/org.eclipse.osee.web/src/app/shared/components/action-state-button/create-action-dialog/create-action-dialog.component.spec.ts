/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { ActionService } from '@osee/shared/services';
import { ActionStateButtonService } from '../internal/services/action-state-button.service';
import { actionStateButtonServiceMock } from '../internal/services/action-state-button.service.mock';
import { ActionUserService } from '../internal/services/action-user.service';
import { MockActionUserService } from '../internal/services/action-user.service.mock';

import { CreateActionDialogComponent } from './create-action-dialog.component';

describe('CreateActionDialogComponent', () => {
	let component: CreateActionDialogComponent;
	let fixture: ComponentFixture<CreateActionDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatFormFieldModule,
				MatSelectModule,
				MatInputModule,
				MatButtonModule,
				MatDialogModule,
				NoopAnimationsModule,
				FormsModule,
				CreateActionDialogComponent,
			],
			providers: [
				{
					provide: ActionUserService,
					useValue: MockActionUserService,
				},
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						originator: {
							id: '',
							name: '',
							guid: null,
							active: false,
							description: null,
							workTypes: [],
							tags: [],
							userId: '',
							email: '',
							loginIds: [],
							savedSearches: [],
							userGroups: [],
							artifactId: '',
							idString: '',
							idIntValue: 0,
							uuid: 0,
						},
						actionableItem: {
							id: '',
							name: '',
						},
						targetedVersion: '',
						title: '',
						description: '',
					},
				},
				{
					provide: ActionStateButtonService,
					useValue: actionStateButtonServiceMock,
				},
				{
					provide: ActionService,
					useValue: {
						ARB: of([
							{
								id: '123',
								name: 'First ARB',
							},
							{
								id: '456',
								name: 'Second ARB',
							},
						]),
						users: of([
							{
								id: '123',
								name: 'user1',
								guid: null,
								active: true,
								description: null,
								workTypes: [],
								tags: [],
								userId: '123',
								email: 'user1@user1domain.com',
								loginIds: ['123'],
								savedSearches: [],
								userGroups: [],
								artifactId: '',
								idString: '123',
								idIntValue: 123,
								uuid: 123,
							},
						]),
					},
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(CreateActionDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
