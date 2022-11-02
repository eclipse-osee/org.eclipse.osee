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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { dialogRef } from '../../../mocks/dialogRef.mock';
import { RemovalDialog } from '../../../types/ConfirmRemovalDialog';

import { ConfirmRemovalDialogComponent } from './confirm-removal-dialog.component';

describe('ConfirmRemovalDialogComponent', () => {
	let component: ConfirmRemovalDialogComponent;
	let fixture: ComponentFixture<ConfirmRemovalDialogComponent>;
	let loader: HarnessLoader;
	let dialogData: RemovalDialog = {
		id: '',
		name: '',
		extraNames: [],
		type: '',
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				MatButtonModule,
				NoopAnimationsModule,
				MatFormFieldModule,
				FormsModule,
			],
			declarations: [ConfirmRemovalDialogComponent],
			providers: [
				{ provide: MatDialogRef, useValue: dialogRef },
				{ provide: MAT_DIALOG_DATA, useValue: dialogData },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ConfirmRemovalDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should close without anything returning', async () => {
		let buttons = await loader.getAllHarnesses(MatButtonHarness);
		let spy = spyOn(component, 'onNoClick').and.callThrough();
		if ((await buttons[0].getText()) === 'Cancel') {
			await buttons[0].click();
			expect(spy).toHaveBeenCalled();
		}
	});
});
