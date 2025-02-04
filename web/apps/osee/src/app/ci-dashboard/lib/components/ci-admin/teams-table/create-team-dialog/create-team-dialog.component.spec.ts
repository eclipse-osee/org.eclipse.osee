/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { CreateTeamDialogComponent } from './create-team-dialog.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ScriptTeam, scriptTeamSentinel } from '../../../../types';

describe('CreateTeamDialogComponent', () => {
	let component: CreateTeamDialogComponent;
	let fixture: ComponentFixture<CreateTeamDialogComponent>;
	const dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
	const dialogData: ScriptTeam = {
		...scriptTeamSentinel,
		name: {
			id: '1234',
			gammaId: '3456',
			typeId: '1152921504606847088',
			value: 'Team 1',
		},
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateTeamDialogComponent],
			providers: [
				provideNoopAnimations(),
				{ provide: MatDialogRef, useValue: dialogRef },
				{ provide: MAT_DIALOG_DATA, useValue: dialogData },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateTeamDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
