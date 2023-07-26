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
import { RemoveArrayElementsDialogComponent } from './remove-array-elements-dialog.component';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { dialogRef } from '@osee/messaging/shared/testing';

describe('RemoveArrayElementsDialogComponent', () => {
	let component: RemoveArrayElementsDialogComponent;
	let fixture: ComponentFixture<RemoveArrayElementsDialogComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				RemoveArrayElementsDialogComponent,
				MatDialogModule,
				MatButtonModule,
			],
			providers: [{ provide: MatDialogRef, useValue: dialogRef }],
		});
		fixture = TestBed.createComponent(RemoveArrayElementsDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
