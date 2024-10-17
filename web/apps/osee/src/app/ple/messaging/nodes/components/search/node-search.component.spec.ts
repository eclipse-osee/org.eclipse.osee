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

import { NodeSearchComponent } from './node-search.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CurrentNodeService } from '@osee/messaging/nodes/components/internal';
import { nodesMock } from '@osee/messaging/shared/testing';
import { of } from 'rxjs';

describe('NodeSearchComponent', () => {
	let component: NodeSearchComponent;
	let fixture: ComponentFixture<NodeSearchComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NodeSearchComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CurrentNodeService,
					useValue: {
						getPaginatedNodesByName() {
							return of(nodesMock);
						},
						getNodesByNameCount() {
							return of(10);
						},
					},
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(NodeSearchComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('selectedNode', nodesMock[0]);
		fixture.componentRef.setInput('protectedNodes', []);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
