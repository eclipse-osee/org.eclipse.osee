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
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CurrentGraphService } from '../../services/current-graph.service';
import { graphServiceMock } from '../../testing/current-graph.service.mock';

import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import type { connection } from '@osee/messaging/shared/types';
import { EditConnectionDialogComponent } from './edit-connection-dialog.component';

import { AsyncPipe, NgFor } from '@angular/common';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';
import { connectionMock, dialogRef } from '@osee/messaging/shared/testing';
import { MockTransportTypeDropdownComponent } from '@osee/messaging/transports/dropdown/testing';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';

describe('EditConnectionDialogComponent', () => {
	let component: EditConnectionDialogComponent;
	let fixture: ComponentFixture<EditConnectionDialogComponent>;
	let loader: HarnessLoader;
	const dialogData: connection = connectionMock;

	beforeEach(async () => {
		await TestBed.overrideComponent(EditConnectionDialogComponent, {
			set: {
				imports: [
					MatDialogModule,
					MatFormFieldModule,
					FormsModule,
					MatInputModule,
					MatSelectModule,
					MockMatOptionLoadingComponent,
					MockApplicabilityDropdownComponent,
					MatOptionModule,
					AsyncPipe,
					NgFor,
					MatButtonModule,
					MockTransportTypeDropdownComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatDialogModule,
					MatInputModule,
					MatSelectModule,
					MatButtonModule,
					NoopAnimationsModule,
					FormsModule,
					EditConnectionDialogComponent,
				],
				providers: [
					{ provide: MatDialogRef, useValue: dialogRef },
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
					{
						provide: CurrentGraphService,
						useValue: graphServiceMock,
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(EditConnectionDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should close without anything returning', async () => {
		const buttons = await loader.getAllHarnesses(MatButtonHarness);
		const spy = spyOn(component, 'onNoClick').and.callThrough();
		if ((await buttons[0].getText()) === 'Cancel') {
			await buttons[0].click();
			expect(spy).toHaveBeenCalled();
		}
	});

	//OBE maybe move to transport type dropdown
	xit('should select a new transport type', async () => {
		const form = await loader.getHarness(
			MatFormFieldHarness.with({
				selector: '#connection-transport-type-selector',
			})
		);
		const select = await form.getControl(MatSelectHarness);
		await select?.open();
		expect((await select?.getOptions())?.length).toEqual(1);
		await select?.clickOptions({ text: 'ETHERNET' });
		expect(await select?.getValueText()).toEqual('ETHERNET');
	});

	it('should enter a description', async () => {
		const form = loader.getHarness(
			MatFormFieldHarness.with({
				selector: '#connection-description-field',
			})
		);
		const input = await (await form).getControl(MatInputHarness);
		expect(await input?.getType()).toEqual('text');
		await input?.setValue('Description');
		expect(await input?.getValue()).toEqual('Description');
	});

	it('should enter a name', async () => {
		const form = loader.getHarness(
			MatFormFieldHarness.with({ selector: '#connection-name-field' })
		);
		const input = await (await form).getControl(MatInputHarness);
		expect(await input?.getType()).toEqual('text');
		await input?.setValue('Name');
		expect(await input?.getValue()).toEqual('Name');
	});
});
