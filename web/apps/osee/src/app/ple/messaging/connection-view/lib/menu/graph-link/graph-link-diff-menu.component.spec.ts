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

import { GraphLinkDiffMenuComponent } from './graph-link-diff-menu.component';
import { graphServiceMock } from '../../testing/current-graph.service.mock';
import { CurrentGraphService } from '../../services/current-graph.service';

describe('GraphLinkDiffMenuComponent', () => {
	let component: GraphLinkDiffMenuComponent;
	let fixture: ComponentFixture<GraphLinkDiffMenuComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [GraphLinkDiffMenuComponent],
			providers: [
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(GraphLinkDiffMenuComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('data', {
			id: '1',
			name: 'edge',
			applicability: {
				id: '1',
				name: 'Base',
			},
			dashed: false,
			description: '',
			transportType: 'ETHERNET',
		});
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
