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
import { MoveArrayElementDialogComponent } from './move-array-element-dialog.component';
import { elementsMock } from '@osee/messaging/shared/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('MoveArrayElementDialogComponent', () => {
	let component: MoveArrayElementDialogComponent;
	let fixture: ComponentFixture<MoveArrayElementDialogComponent>;
	const dialogData = {
		element: elementsMock[0],
		parent: elementsMock[1],
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MoveArrayElementDialogComponent],
			providers: [
				provideNoopAnimations(),
				{ provide: MAT_DIALOG_DATA, useValue: dialogData },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MoveArrayElementDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
