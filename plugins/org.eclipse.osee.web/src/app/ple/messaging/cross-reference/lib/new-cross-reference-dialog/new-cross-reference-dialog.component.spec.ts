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
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { dialogRef } from '@osee/messaging/shared/testing';
import type { CrossReference } from '@osee/messaging/shared/types';
import { of } from 'rxjs';
import { NewCrossReferenceDialogComponent } from './new-cross-reference-dialog.component';
import { applicabilitySentinel } from '@osee/shared/types/applicability';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { MockApplicabilitySelectorComponent } from '@osee/shared/components/testing';

describe('NewCrossReferenceDialogComponent', () => {
	let component: NewCrossReferenceDialogComponent;
	let fixture: ComponentFixture<NewCrossReferenceDialogComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(NewCrossReferenceDialogComponent, {
			remove: {
				imports: [ApplicabilitySelectorComponent],
			},
			add: {
				imports: [MockApplicabilitySelectorComponent],
			},
		})
			.configureTestingModule({
				imports: [
					MatDialogModule,
					NewCrossReferenceDialogComponent,
					NoopAnimationsModule,
				],
				providers: [
					{ provide: MatDialogRef, useValue: dialogRef },
					{
						provide: MAT_DIALOG_DATA,
						useValue: of<CrossReference>({
							id: '1234567890',
							name: 'CR',
							crossReferenceValue: 'Val',
							crossReferenceArrayValues: '0=test;1=testing',
							crossReferenceAdditionalContent:
								'Additional Content',
							connections: [],
							applicability: applicabilitySentinel,
						}),
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(NewCrossReferenceDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
