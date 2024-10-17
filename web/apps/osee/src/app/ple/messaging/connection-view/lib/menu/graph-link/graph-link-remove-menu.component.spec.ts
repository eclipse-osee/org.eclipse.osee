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

import { GraphLinkRemoveMenuComponent } from './graph-link-remove-menu.component';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { CurrentGraphService } from '../../services/current-graph.service';
import {
	ethernetTransportType,
	nodesMock,
} from '@osee/messaging/shared/testing';

describe('GraphLinkRemoveMenuComponent', () => {
	let component: GraphLinkRemoveMenuComponent;
	let fixture: ComponentFixture<GraphLinkRemoveMenuComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [GraphLinkRemoveMenuComponent],
			providers: [
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(GraphLinkRemoveMenuComponent);
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
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
