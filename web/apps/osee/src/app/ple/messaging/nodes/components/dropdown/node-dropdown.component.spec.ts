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

import { NodeDropdownComponent } from './node-dropdown.component';
import { CurrentNodeService } from '../internal/current-node.service';
import {
	ethernetTransportType,
	nodesMock,
} from '@osee/messaging/shared/testing';
import { of } from 'rxjs';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

describe('NodeDropdownComponent', () => {
	let component: ParentDriverComponent;
	let fixture: ComponentFixture<ParentDriverComponent>;
	@Component({
		selector: 'osee-test-standalone-form',
		imports: [FormsModule, NodeDropdownComponent],
		template: `<form #testForm="ngForm">
			<osee-node-dropdown
				[(selectedNodes)]="nodes"
				[transportType]="transportType()" />
		</form>`,
	})
	class ParentDriverComponent {
		nodes = signal([]);
		transportType = signal(ethernetTransportType);
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NodeDropdownComponent],
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

		fixture = TestBed.createComponent(ParentDriverComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
