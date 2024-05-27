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
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInput } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

import { NodeDropdownComponent } from '@osee/messaging/nodes/dropdown';
import { dialogRef, nodesMock } from '@osee/messaging/shared/testing';
import { MockTransportTypeDropdownComponent } from '@osee/messaging/transports/dropdown/testing';
import { CreateConnectionDialogComponent } from './create-connection-dialog.component';
import { nodeData } from '@osee/messaging/shared/types';

describe('CreateConnectionDialogComponent', () => {
	let component: CreateConnectionDialogComponent;
	let fixture: ComponentFixture<CreateConnectionDialogComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		const dialogData: nodeData = nodesMock[0];
		await TestBed.overrideComponent(CreateConnectionDialogComponent, {
			set: {
				providers: [
					{ provide: MatDialogRef, useValue: dialogRef },
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
				],
				imports: [
					MatDialogTitle,
					MatDialogContent,
					MatDialogActions,
					MatDialogClose,
					MatFormField,
					MatLabel,
					MatError,
					FormsModule,
					MatInput,
					MatButton,
					NodeDropdownComponent,
					MockTransportTypeDropdownComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [CreateConnectionDialogComponent],
				declarations: [],
				providers: [
					provideNoopAnimations(),
					{ provide: MatDialogRef, useValue: dialogRef },
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(CreateConnectionDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	// OBE maybe move to transport-type-dropdown component
	xit('should select a new transport type', async () => {
		const form = loader.getHarness(
			MatFormFieldHarness.with({
				selector: '#connection-transport-type-selector',
			})
		);
		const select = await (await form).getControl(MatSelectHarness);
		await select?.open();
		expect((await select?.getOptions())?.length).toEqual(1);
		await select?.clickOptions({ text: 'ETHERNET' });
		expect(await select?.getValueText()).toEqual('ETHERNET');
	});

	// OBE maybe move to node-dropdown component
	xit('should select a new node to connect from', async () => {
		// component.transportType.set(ethernetTransportType);
		const form = loader.getHarness(
			MatFormFieldHarness.with({
				selector: '#connection-from-node-selector',
			})
		);
		const select = await (await form).getControl(MatSelectHarness);
		await select?.open();
		expect((await select?.getOptions())?.length).toEqual(2);
		await select?.clickOptions({ text: 'Second' });
		expect(await select?.getValueText()).toEqual('Second');
	});

	// OBE maybe move to node-dropdown component
	xit('should select a new node to connect to', async () => {
		// component.transportType.set(ethernetTransportType);
		const form = loader.getHarness(
			MatFormFieldHarness.with({
				selector: '#connection-to-node-selector',
			})
		);
		const select = await (await form).getControl(MatSelectHarness);
		await select?.open();
		expect((await select?.getOptions())?.length).toEqual(2);
		await select?.clickOptions({ text: 'Second' });
		expect(await select?.getValueText()).toEqual('Second');
	});

	// OBE maybe move to node-dropdown component
	xit('should select connection nodes', async () => {
		// component.transportType.set(ethernetTransportType);
		const form = loader.getHarness(
			MatFormFieldHarness.with({
				selector: '#connection-node-selector',
			})
		);
		const select = await (await form).getControl(MatSelectHarness);
		await select?.open();
		expect((await select?.getOptions())?.length).toEqual(2);
		await select?.clickOptions({ text: 'Second' });
		expect(await select?.getValueText()).toEqual('Second');
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
