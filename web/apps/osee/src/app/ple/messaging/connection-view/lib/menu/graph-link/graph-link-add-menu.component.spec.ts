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

import { GraphLinkAddMenuComponent } from './graph-link-add-menu.component';
import {
	ethernetTransportType,
	nodesMock,
} from '@osee/messaging/shared/testing';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { CurrentGraphService } from '../../services/current-graph.service';

describe('GraphLinkAddMenuComponent', () => {
	let component: GraphLinkAddMenuComponent;
	let fixture: ComponentFixture<GraphLinkAddMenuComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [GraphLinkAddMenuComponent],
			providers: [
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(GraphLinkAddMenuComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('data', {
			id: '3',
			name: '3',
			description: '',
			transportType: ethernetTransportType,
			nodes: nodesMock,
		});
		fixture.componentRef.setInput('editMode', false);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
