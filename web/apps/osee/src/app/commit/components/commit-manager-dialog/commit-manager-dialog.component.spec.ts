/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { CommitManagerDialogComponent } from './commit-manager-dialog.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { teamWorkflowDetailsMock } from '@osee/shared/testing';
import { ActionService } from '@osee/configuration-management/services';
import { actionServiceMock } from '@osee/configuration-management/testing';

describe('CommitManagerDialogComponent', () => {
	let component: CommitManagerDialogComponent;
	let fixture: ComponentFixture<CommitManagerDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CommitManagerDialogComponent],
			providers: [
				{
					provide: MAT_DIALOG_DATA,
					useValue: teamWorkflowDetailsMock,
				},
				{
					provide: ActionService,
					useValue: actionServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CommitManagerDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
