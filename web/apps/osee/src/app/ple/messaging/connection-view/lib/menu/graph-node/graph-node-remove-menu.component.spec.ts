/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { CurrentGraphService } from '../../services/current-graph.service';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { GraphNodeRemoveMenuComponent } from './graph-node-remove-menu.component';
import {
	ethernetTransportType,
	nodesMock,
} from '@osee/messaging/shared/testing';
import { nodeData } from '@osee/messaging/shared/types';
import { applicabilitySentinel } from '@osee/applicability/types';

describe('GraphNodeRemoveMenuComponent', () => {
	let component: GraphNodeRemoveMenuComponent;
	let fixture: ComponentFixture<GraphNodeRemoveMenuComponent>;
	const testNode: nodeData = nodesMock[0];
	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [GraphNodeRemoveMenuComponent],
			providers: [
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(GraphNodeRemoveMenuComponent);
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
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
