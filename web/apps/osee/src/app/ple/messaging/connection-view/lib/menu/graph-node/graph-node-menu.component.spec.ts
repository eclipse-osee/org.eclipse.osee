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
import { provideRouter } from '@angular/router';
import { CurrentGraphService } from '../../services/current-graph.service';
import { graphServiceMock } from '../../testing/current-graph.service.mock';

import {
	ethernetTransportType,
	nodesMock,
} from '@osee/messaging/shared/testing';
import type { nodeData } from '@osee/messaging/shared/types';
import { GraphNodeMenuComponent } from './graph-node-menu.component';
import { applicabilitySentinel } from '@osee/applicability/types';

describe('GraphNodeMenuComponent', () => {
	let component: GraphNodeMenuComponent;
	let fixture: ComponentFixture<GraphNodeMenuComponent>;
	let loader: HarnessLoader;

	const testNode: nodeData = nodesMock[0];

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [GraphNodeMenuComponent],
			providers: [
				provideNoopAnimations(),
				provideRouter([]),
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(GraphNodeMenuComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('editMode', true);
		fixture.componentRef.setInput('data', testNode);
		fixture.componentRef.setInput('sources', [
			{
				source: '1',
				target: '2',
				label: '3',
				data: {
					name: '3',
					description: '',
					transportType: ethernetTransportType,
					nodes: nodesMock,
					applicability: applicabilitySentinel,
				},
			},
		]);
		fixture.componentRef.setInput('targets', [
			{
				source: '4',
				target: '1',
				label: '4',
				data: {
					name: '4',
					description: '',
					transportType: ethernetTransportType,
					nodes: nodesMock,
					applicability: applicabilitySentinel,
				},
			},
		]);
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('Editing Enabled & no changes', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('editMode', true);
			fixture.componentRef.setInput('data', testNode);
			fixture.componentRef.setInput('sources', [
				{
					source: '1',
					target: '2',
					label: '3',
					data: {
						name: '3',
						description: '',
						transportType: ethernetTransportType,
						nodes: nodesMock,
						applicability: applicabilitySentinel,
					},
				},
			]);
			fixture.componentRef.setInput('targets', [
				{
					source: '4',
					target: '1',
					label: '4',
					data: {
						name: '4',
						description: '',
						transportType: ethernetTransportType,
						nodes: nodesMock,
						applicability: applicabilitySentinel,
					},
				},
			]);
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		it('should have the correct amount of items(3)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(3);
		});
	});

	describe('Editing Enabled & changes', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('editMode', true);
			fixture.componentRef.setInput('data', {
				...testNode,
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
					interfaceNodeBackgroundColor: {
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
			});
			fixture.componentRef.setInput('sources', [
				{
					source: '1',
					target: '2',
					label: '3',
					data: {
						name: '3',
						description: '',
						transportType: ethernetTransportType,
						nodes: nodesMock,
						applicability: applicabilitySentinel,
					},
				},
			]);
			fixture.componentRef.setInput('targets', [
				{
					source: '4',
					target: '1',
					label: '4',
					data: {
						name: '4',
						description: '',
						transportType: ethernetTransportType,
						nodes: nodesMock,
						applicability: applicabilitySentinel,
					},
				},
			]);
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});

		it('should have the correct amount of items(4)', async () => {
			const buttons = await loader.getAllHarnesses(MatMenuItemHarness);
			expect(buttons.length).toEqual(4);
		});
	});

	describe('Editing Disabled & no changes', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('editMode', false);
			fixture.componentRef.setInput('data', testNode);
			fixture.componentRef.setInput('sources', [
				{
					source: '1',
					target: '2',
					label: '3',
					data: {
						name: '3',
						description: '',
						transportType: ethernetTransportType,
						nodes: nodesMock,
						applicability: applicabilitySentinel,
					},
				},
			]);
			fixture.componentRef.setInput('targets', [
				{
					source: '4',
					target: '1',
					label: '4',
					data: {
						name: '4',
						description: '',
						transportType: ethernetTransportType,
						nodes: nodesMock,
						applicability: applicabilitySentinel,
					},
				},
			]);
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});
	});

	describe('Editing Disabled & changes', () => {
		beforeEach(() => {
			fixture.componentRef.setInput('editMode', false);
			fixture.componentRef.setInput('data', {
				...testNode,
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
					interfaceNodeBackgroundColor: {
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
			});
			fixture.componentRef.setInput('sources', [
				{
					source: '1',
					target: '2',
					label: '3',
					data: {
						name: '3',
						description: '',
						transportType: ethernetTransportType,
						nodes: nodesMock,
						applicability: applicabilitySentinel,
					},
				},
			]);
			fixture.componentRef.setInput('targets', [
				{
					source: '4',
					target: '1',
					label: '4',
					data: {
						name: '4',
						description: '',
						transportType: ethernetTransportType,
						nodes: nodesMock,
						applicability: applicabilitySentinel,
					},
				},
			]);
			fixture.detectChanges();
			expect(component).toBeTruthy();
		});
	});
});
