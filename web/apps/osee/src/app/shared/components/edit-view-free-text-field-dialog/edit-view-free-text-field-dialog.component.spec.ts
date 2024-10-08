/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { MimPreferencesService } from '@osee/messaging/shared/services';
import { MimPreferencesServiceMock } from '@osee/messaging/shared/testing';
import { of } from 'rxjs';

import { EditViewFreeTextFieldDialogComponent } from './edit-view-free-text-field-dialog.component';

describe('EditViewFreeTextFieldDialogComponent', () => {
	let component: EditViewFreeTextFieldDialogComponent;
	let fixture: ComponentFixture<EditViewFreeTextFieldDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				FormsModule,
				MatIconModule,
				MatInputModule,
				MatDialogModule,
				MatFormFieldModule,
				NoopAnimationsModule,
				EditViewFreeTextFieldDialogComponent,
			],
			providers: [
				{
					provide: MatDialogRef,
					useValue: {
						close() {
							return of();
						},
					},
				},
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						original: 'abcdef',
						type: 'Description',
						return: 'abcdef',
						editable: true,
					},
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{
					provide: MimPreferencesService,
					useValue: MimPreferencesServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(EditViewFreeTextFieldDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
