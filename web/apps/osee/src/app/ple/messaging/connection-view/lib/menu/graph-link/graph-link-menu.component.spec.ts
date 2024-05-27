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
import { MatMenuItemHarness } from '@angular/material/menu/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CurrentGraphService } from '../../services/current-graph.service';
import { graphServiceMock } from '../../testing/current-graph.service.mock';

import { provideRouter } from '@angular/router';
import {
	ethernetTransportType,
	nodesMock,
} from '@osee/messaging/shared/testing';
import { GraphLinkMenuComponent } from './graph-link-menu.component';

describe('GraphLinkMenuComponent', () => {
	let component: GraphLinkMenuComponent;
	let fixture: ComponentFixture<GraphLinkMenuComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [GraphLinkMenuComponent],
			providers: [
				provideNoopAnimations(),
				provideRouter([]),
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(GraphLinkMenuComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('data', {
			id: '3',
			name: '3',
			description: '',
			transportType: ethernetTransportType,
			nodes: nodesMock,
		});
		fixture.componentRef.setInput('editMode', false);
		fixture.componentRef.setInput('source', {
			id: '1',
			data: {
				id: '1',
				name: '1',
				interfaceNodeAddress: '',
				interfaceNodeBackgroundColor: '',
			},
		});
		fixture.componentRef.setInput('target', {
			id: '2',
			data: {
				id: '2',
				name: '2',
				interfaceNodeAddress: '',
				interfaceNodeBackgroundColor: '',
			},
		});
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('With Editing Enabled & no changes', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('editMode', true);
			fixture.componentRef.setInput('data', {
				id: '3',
				name: '3',
				description: '',
				transportType: ethernetTransportType,
				nodes: nodesMock,
			});
			fixture.componentRef.setInput('source', {
				id: '1',
				data: {
					id: '1',
					name: '1',
					interfaceNodeAddress: '',
					interfaceNodeBackgroundColor: '',
				},
			});
			fixture.componentRef.setInput('target', {
				id: '2',
				data: {
					id: '2',
					name: '2',
					interfaceNodeAddress: '',
					interfaceNodeBackgroundColor: '',
				},
			});
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});
		//these tests will be OBE once we align with material spec as material 3 spec suggests options should be disabled rather than hidden https://m3.material.io/components/menus/guidelines#4d29725a-93dd-4f5e-b255-e24f27678984
		it('Should have the correct amount of items(6)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(6);
		});
	});

	describe('With Editing Enabled & changes', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('editMode', true);
			fixture.componentRef.setInput('data', {
				id: '3',
				name: '3',
				description: '',
				applicability: { id: '1', name: 'Base' },
				transportType: ethernetTransportType,
				nodes: nodesMock,
				changes: {
					name: {
						previousValue: 'a',
						currentValue: '3',
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
							name: 'Random Applicability',
						},
						currentValue: { id: '1', name: 'Base' },
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					transportType: {
						previousValue: 'MILSTD1553_B',
						currentValue: 'ETHERNET',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
				},
			});
			fixture.componentRef.setInput('source', {
				id: '1',
				data: {
					id: '1',
					name: '1',
					interfaceNodeAddress: '',
					interfaceNodeBackgroundColor: '',
				},
			});
			fixture.componentRef.setInput('target', {
				id: '2',
				data: {
					id: '2',
					name: '2',
					interfaceNodeAddress: '',
					interfaceNodeBackgroundColor: '',
				},
			});
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		//this test is OBE with updates to material spec
		it('Should have the correct amount of items(6)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(6);
		});
	});

	describe('With Editing Disabled & no changes', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('editMode', false);
			fixture.componentRef.setInput('data', {
				id: '3',
				name: '3',
				description: '',
				transportType: ethernetTransportType,
				nodes: nodesMock,
			});
			fixture.componentRef.setInput('source', {
				id: '1',
				data: {
					id: '1',
					name: '1',
					interfaceNodeAddress: '',
					interfaceNodeBackgroundColor: '',
				},
			});
			fixture.componentRef.setInput('target', {
				id: '2',
				data: {
					id: '2',
					name: '2',
					interfaceNodeAddress: '',
					interfaceNodeBackgroundColor: '',
				},
			});
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});
		//these tests will be OBE once we align with material spec as material 3 spec suggests options should be disabled rather than hidden https://m3.material.io/components/menus/guidelines#4d29725a-93dd-4f5e-b255-e24f27678984
		it('Should have the correct amount of items(6)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(6);
		});
	});

	describe('With Editing Disabled & changes', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('editMode', false);
			fixture.componentRef.setInput('data', {
				id: '3',
				name: '3',
				description: '',
				applicability: { id: '1', name: 'Base' },
				transportType: ethernetTransportType,
				nodes: nodesMock,
				changes: {
					name: {
						previousValue: 'a',
						currentValue: '3',
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
							name: 'Random Applicability',
						},
						currentValue: { id: '1', name: 'Base' },
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
					transportType: {
						previousValue: 'MILSTD1553_B',
						currentValue: 'ETHERNET',
						transactionToken: {
							id: '-1',
							branchId: '-1',
						},
					},
				},
			});
			fixture.componentRef.setInput('source', {
				id: '1',
				data: {
					id: '1',
					name: '1',
					interfaceNodeAddress: '',
					interfaceNodeBackgroundColor: '',
				},
			});
			fixture.componentRef.setInput('target', {
				id: '2',
				data: {
					id: '2',
					name: '2',
					interfaceNodeAddress: '',
					interfaceNodeBackgroundColor: '',
				},
			});
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		it('Should have the correct amount of items(6)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(6);
		});
	});
});
