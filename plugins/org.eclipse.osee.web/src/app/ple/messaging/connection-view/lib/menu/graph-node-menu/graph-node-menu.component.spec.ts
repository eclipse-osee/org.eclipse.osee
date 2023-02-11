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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { difference } from '@osee/shared/types/change-report';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { CurrentGraphService } from '../../services/current-graph.service';

import { GraphNodeMenuComponent } from './graph-node-menu.component';
import {
	EnumsService,
	nodeDataWithChanges,
	_newConnection,
} from '@osee/messaging/shared';
import { enumsServiceMock } from '@osee/messaging/shared/testing';

describe('GraphNodeMenuComponent', () => {
	let component: GraphNodeMenuComponent;
	let fixture: ComponentFixture<GraphNodeMenuComponent>;
	let loader: HarnessLoader;
	let router: any;
	let route: any;

	beforeEach(async () => {
		router = jasmine.createSpyObj(
			'Router',
			['navigate', 'createUrlTree', 'serializeUrl'],
			{ url: new String() }
		);
		route = jasmine.createSpyObj('ActivatedRoute', [], { parent: '' });
		await TestBed.configureTestingModule({
			imports: [
				MatButtonModule,
				MatIconModule,
				MatDialogModule,
				NoopAnimationsModule,
				RouterTestingModule,
				MatMenuModule,
				MatFormFieldModule,
				FormsModule,
				MatSelectModule,
				MatInputModule,
				CommonModule,
				GraphNodeMenuComponent,
			],
			providers: [
				{ provide: Router, useValue: router },
				{ provide: ActivatedRoute, useValue: route },
				{ provide: CurrentGraphService, useValue: graphServiceMock },
				{ provide: EnumsService, useValue: enumsServiceMock },
			],
			declarations: [],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(GraphNodeMenuComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('Editing Enabled & no changes', () => {
		beforeEach(() => {
			component.editMode = true;
			component.data = {
				id: '1',
				name: '1',
				interfaceNodeAddress: '',
				interfaceNodeBgColor: '',
				description: '',
				applicability: {
					id: '1',
					name: 'Base',
				},
			};
			component.sources = [
				{
					source: '1',
					target: '2',
					label: '3',
					data: {
						name: '3',
						description: '',
						transportType: {
							name: 'ETHERNET',
							byteAlignValidation: false,
							byteAlignValidationSize: 0,
							messageGeneration: false,
							messageGenerationPosition: '',
							messageGenerationType: '',
						},
					},
				},
			];
			component.targets = [
				{
					source: '4',
					target: '1',
					label: '4',
					data: {
						name: '4',
						description: '',
						transportType: {
							name: 'ETHERNET',
							byteAlignValidation: false,
							byteAlignValidationSize: 0,
							messageGeneration: false,
							messageGenerationPosition: '',
							messageGenerationType: '',
						},
					},
				},
			];
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		it('should have the correct amount of items(3)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(3);
		});

		it('should open the edit node dialog', async () => {
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(component.data),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(component, 'openEditNodeDialog').and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('Edit ' + component.data.name),
					})
				)
			).click();
			expect(spy).toHaveBeenCalledWith(component.data);
		});

		it('should open the remove node & connection dialog', async () => {
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({
					id: component.data.id,
					name: component.data.name,
					extraNames: [
						component.sources[0].label,
						component.targets[0].label,
					],
					type: 'node',
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(
				component,
				'removeNodeAndConnection'
			).and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp(
							'Remove ' + component.data.name + ' & Connection'
						),
					})
				)
			).click();
			expect(spy).toHaveBeenCalledWith(
				component.data,
				[component.sources[0]],
				[component.targets[0]]
			);
		});

		it('should open the create connection dialog', async () => {
			const conn: _newConnection = {
				name: '',
				description: '',
				transportType: {
					name: 'ETHERNET',
					byteAlignValidation: false,
					byteAlignValidationSize: 0,
					messageGeneration: false,
					messageGenerationPosition: '',
					messageGenerationType: '',
				},
			};
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(conn),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(
				component,
				'createConnectionToNode'
			).and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp(
							'Create Connection To ' + component.data.name
						),
					})
				)
			).click();
			expect(spy).toHaveBeenCalledWith(component.data);
		});
	});

	describe('Editing Enabled & changes', () => {
		beforeEach(() => {
			component.editMode = true;
			component.data = {
				id: '1',
				name: '1',
				interfaceNodeAddress: '',
				interfaceNodeBgColor: '',
				description: '',
				applicability: {
					id: '1',
					name: 'Base',
				},
				changes: {
					name: {
						previousValue: '7',
						currentValue: '1',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					interfaceNodeAddress: {
						previousValue: 'abcdef',
						currentValue: '',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					interfaceNodeBgColor: {
						previousValue: 'abcdef',
						currentValue: '',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					description: {
						previousValue: 'abcdef',
						currentValue: '',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					applicability: {
						previousValue: {
							id: '2',
							name: 'Random applicability',
						},
						currentValue: {
							id: '1',
							name: 'Base',
						},
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
				},
			};
			component.sources = [
				{
					source: '1',
					target: '2',
					label: '3',
					data: {
						name: '3',
						description: '',
						transportType: {
							name: 'ETHERNET',
							byteAlignValidation: false,
							byteAlignValidationSize: 0,
							messageGeneration: false,
							messageGenerationPosition: '',
							messageGenerationType: '',
						},
					},
				},
			];
			component.targets = [
				{
					source: '4',
					target: '1',
					label: '4',
					data: {
						name: '4',
						description: '',
						transportType: {
							name: 'ETHERNET',
							byteAlignValidation: false,
							byteAlignValidationSize: 0,
							messageGeneration: false,
							messageGenerationPosition: '',
							messageGenerationType: '',
						},
					},
				},
			];
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		it('should have the correct amount of items(4)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(4);
		});

		it('should open the edit node dialog', async () => {
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(component.data),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(component, 'openEditNodeDialog').and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('Edit ' + component.data.name),
					})
				)
			).click();
			expect(spy).toHaveBeenCalledWith(component.data);
		});

		it('should open the remove node & connection dialog', async () => {
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({
					id: component.data.id,
					name: component.data.name,
					extraNames: [
						component.sources[0].label,
						component.targets[0].label,
					],
					type: 'node',
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(
				component,
				'removeNodeAndConnection'
			).and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp(
							'Remove ' + component.data.name + ' & Connection'
						),
					})
				)
			).click();
			expect(spy).toHaveBeenCalledWith(
				component.data,
				[component.sources[0]],
				[component.targets[0]]
			);
		});

		it('should open the create connection dialog', async () => {
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({ data: component.data }),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let spy = spyOn(
				component,
				'createConnectionToNode'
			).and.callThrough();
			await (
				await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp(
							'Create Connection To ' + component.data.name
						),
					})
				)
			).click();
			expect(spy).toHaveBeenCalledWith(component.data);
		});

		describe('opening the diff sidenav', () => {
			let spy: jasmine.Spy<
				(open: boolean, value: difference, header: string) => void
			>;
			beforeEach(() => {
				spy = spyOn(component, 'viewDiff').and.callThrough();
			});

			it('should open the name sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Name' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes.name || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Name'
				);
			});

			it('should open the description sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Description' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes
						.description || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Description'
				);
			});

			it('should open the address/port sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Address/Port' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes
						.interfaceNodeAddress || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Address/Port'
				);
			});

			it('should open the background color sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Background Color' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes
						.interfaceNodeBgColor || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Background Color'
				);
			});

			it('should open the applicability sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Applicability' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes
						.applicability || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Applicability'
				);
			});
		});
	});

	describe('Editing Disabled & no changes', () => {
		beforeEach(() => {
			component.editMode = false;
			component.data = {
				id: '1',
				name: '1',
				interfaceNodeAddress: '',
				interfaceNodeBgColor: '',
				description: '',
				applicability: {
					id: '1',
					name: 'Base',
				},
			};
			component.sources = [
				{
					source: '1',
					target: '2',
					label: '3',
					data: {
						name: '3',
						description: '',
						transportType: {
							name: 'ETHERNET',
							byteAlignValidation: false,
							byteAlignValidationSize: 0,
							messageGeneration: false,
							messageGenerationPosition: '',
							messageGenerationType: '',
						},
					},
				},
			];
			component.targets = [
				{
					source: '4',
					target: '1',
					label: '4',
					data: {
						name: '4',
						description: '',
						transportType: {
							name: 'ETHERNET',
							byteAlignValidation: false,
							byteAlignValidationSize: 0,
							messageGeneration: false,
							messageGenerationPosition: '',
							messageGenerationType: '',
						},
					},
				},
			];
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		it('should have the correct amount of items(1)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(1);
		});

		it('should have no options text', async () => {
			const item = await loader.getHarness(MatMenuItemHarness);
			expect(await item.getText()).toEqual('No options available.');
		});
	});

	describe('Editing Disabled & changes', () => {
		beforeEach(() => {
			component.editMode = false;
			component.data = {
				id: '1',
				name: '1',
				interfaceNodeAddress: '',
				interfaceNodeBgColor: '',
				description: '',
				applicability: {
					id: '1',
					name: 'Base',
				},
				changes: {
					name: {
						previousValue: '7',
						currentValue: '1',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					interfaceNodeAddress: {
						previousValue: 'abcdef',
						currentValue: '',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					interfaceNodeBgColor: {
						previousValue: 'abcdef',
						currentValue: '',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					description: {
						previousValue: 'abcdef',
						currentValue: '',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					applicability: {
						previousValue: {
							id: '2',
							name: 'Random applicability',
						},
						currentValue: {
							id: '1',
							name: 'Base',
						},
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
				},
			};
			component.sources = [
				{
					source: '1',
					target: '2',
					label: '3',
					data: {
						name: '3',
						description: '',
						transportType: {
							name: 'ETHERNET',
							byteAlignValidation: false,
							byteAlignValidationSize: 0,
							messageGeneration: false,
							messageGenerationPosition: '',
							messageGenerationType: '',
						},
					},
				},
			];
			component.targets = [
				{
					source: '4',
					target: '1',
					label: '4',
					data: {
						name: '4',
						description: '',
						transportType: {
							name: 'ETHERNET',
							byteAlignValidation: false,
							byteAlignValidationSize: 0,
							messageGeneration: false,
							messageGenerationPosition: '',
							messageGenerationType: '',
						},
					},
				},
			];
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});
		it('should have the correct amount of items(1)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(1);
		});

		describe('opening the diff sidenav', () => {
			let spy: jasmine.Spy<
				(open: boolean, value: difference, header: string) => void
			>;
			beforeEach(() => {
				spy = spyOn(component, 'viewDiff').and.callThrough();
			});

			it('should open the name sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Name' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes.name || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Name'
				);
			});

			it('should open the description sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Description' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes
						.description || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Description'
				);
			});

			it('should open the address/port sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Address/Port' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes
						.interfaceNodeAddress || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Address/Port'
				);
			});

			it('should open the background color sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Background Color' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes
						.interfaceNodeBgColor || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Background Color'
				);
			});

			it('should open the applicability sidenav', async () => {
				const topItem = await loader.getHarness(
					MatMenuItemHarness.with({
						text: new RegExp('View Diff for'),
					})
				);
				if (await topItem.hasSubmenu()) {
					const subMenu = await topItem.getSubmenu();
					await subMenu?.clickItem({ text: 'Applicability' });
				}
				expect(spy).toHaveBeenCalledWith(
					true,
					(component.data as nodeDataWithChanges).changes
						.applicability || {
						previousValue: '',
						currentValue: '',
						transactionToken: { id: '-1', branchId: '-1' },
					},
					'Applicability'
				);
			});
		});
	});
});
