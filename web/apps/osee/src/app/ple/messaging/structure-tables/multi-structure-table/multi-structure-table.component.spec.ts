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
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AsyncPipe } from '@angular/common';
import { provideRouter } from '@angular/router';
import { CurrentStructureMultiService } from '@osee/messaging/shared/services';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { MULTI_STRUCTURE_SERVICE } from '@osee/messaging/shared/tokens';
import { MockStructureInterfaceComponent } from '../lib/structure-interface/structure-interface.component.mock';
import { MultiStructureTableComponent } from './multi-structure-table.component';

describe('MultiStructureComponent', () => {
	let component: MultiStructureTableComponent;
	let fixture: ComponentFixture<MultiStructureTableComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MultiStructureTableComponent, {
			set: {
				imports: [AsyncPipe, MockStructureInterfaceComponent],
				providers: [
					{
						provide: CurrentStructureMultiService,
						useValue: CurrentStateServiceMock,
					},
					MULTI_STRUCTURE_SERVICE,
				],
			},
		})
			.configureTestingModule({
				providers: [provideRouter([])],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(MultiStructureTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
