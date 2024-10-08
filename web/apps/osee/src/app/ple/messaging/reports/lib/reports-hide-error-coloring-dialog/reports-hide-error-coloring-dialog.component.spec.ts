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
import { ReportsHideErrorColoringDialogComponent } from './reports-hide-error-coloring-dialog.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { connectionValidationResponseMock } from '@osee/messaging/shared/testing';

describe('ReportsHideErrorColoringDialogComponent', () => {
	let component: ReportsHideErrorColoringDialogComponent;
	let fixture: ComponentFixture<ReportsHideErrorColoringDialogComponent>;
	const dialogData = connectionValidationResponseMock;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ReportsHideErrorColoringDialogComponent],
			providers: [{ provide: MAT_DIALOG_DATA, useValue: dialogData }],
		}).compileComponents();

		fixture = TestBed.createComponent(
			ReportsHideErrorColoringDialogComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
