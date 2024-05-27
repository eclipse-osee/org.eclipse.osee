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
import { TransportTypeDropdownComponent } from './transport-type-dropdown.component';
import { CurrentTransportTypeService } from './current-transport-type.service';
import {
	ethernetTransportType,
	transportTypes,
} from '@osee/messaging/shared/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';

describe('TransportTypeDropdownComponent', () => {
	let component: TransportTypeDropdownComponent;
	let fixture: ComponentFixture<TransportTypeDropdownComponent>;
	const currentTransportTypeServiceMock: Partial<CurrentTransportTypeService> =
		{
			getPaginatedTypes(_pageNum, _pageSize) {
				return of(transportTypes);
			},
		};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [TransportTypeDropdownComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CurrentTransportTypeService,
					useValue: currentTransportTypeServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(TransportTypeDropdownComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('transportType', ethernetTransportType);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
