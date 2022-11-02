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
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { graphServiceMock } from '../../../mocks/CurrentGraphService.mock';
import { dialogRef } from '../../../mocks/dialogRef.mock';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { node } from '../../../../shared/types/node';

import { EditNodeDialogComponent } from './edit-node-dialog.component';

describe('EditNodeDialogComponent', () => {
	let component: EditNodeDialogComponent;
	let fixture: ComponentFixture<EditNodeDialogComponent>;
	let loader: HarnessLoader;
	let dialogData: node = {
		name: '',
		applicability: { id: '1', name: 'Base' },
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				MatFormFieldModule,
				MatSelectModule,
				MatInputModule,
				MatButtonModule,
				NoopAnimationsModule,
				FormsModule,
			],
			declarations: [EditNodeDialogComponent],
			providers: [
				{ provide: MatDialogRef, useValue: dialogRef },
				{ provide: MAT_DIALOG_DATA, useValue: dialogData },
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(EditNodeDialogComponent);
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
