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
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatOptionLoadingModule } from '../../../../../../shared-components/mat-option-loading/mat-option-loading.module';
import { dialogRef } from '../../../../connection-view/mocks/dialogRef.mock';

import { NewTransportTypeDialogComponent } from './new-transport-type-dialog.component';

describe('NewTransportTypeDialogComponent', () => {
	let component: NewTransportTypeDialogComponent;
	let fixture: ComponentFixture<NewTransportTypeDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				MatFormFieldModule,
				MatInputModule,
				MatSelectModule,
				MatOptionLoadingModule,
				MatButtonModule,
				FormsModule,
				NoopAnimationsModule,
				MatSlideToggleModule,
			],
			providers: [{ provide: MatDialogRef, useValue: dialogRef }],
			declarations: [NewTransportTypeDialogComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(NewTransportTypeDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
