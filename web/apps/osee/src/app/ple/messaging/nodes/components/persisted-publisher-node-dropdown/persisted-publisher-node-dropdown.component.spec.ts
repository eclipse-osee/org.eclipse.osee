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

import { PersistedPublisherNodeDropdownComponent } from './persisted-publisher-node-dropdown.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
	TransportTypeUiService,
	NodeService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import {
	transportTypeUIServiceMock,
	nodeServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';

describe('PersistedPublisherNodeDropdownComponent', () => {
	let component: PersistedPublisherNodeDropdownComponent;
	let fixture: ComponentFixture<PersistedPublisherNodeDropdownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(
			PersistedPublisherNodeDropdownComponent,
			{
				add: {
					providers: [
						{
							provide: WarningDialogService,
							useValue: warningDialogServiceMock,
						},
					],
				},
			}
		)
			.configureTestingModule({
				imports: [PersistedPublisherNodeDropdownComponent],
				providers: [
					provideNoopAnimations(),
					{
						provide: TransportTypeUiService,
						useValue: transportTypeUIServiceMock,
					},
					{
						provide: CurrentTransactionService,
						useValue: currentTransactionServiceMock,
					},
					{
						provide: NodeService,
						useValue: nodeServiceMock,
					},
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(
			PersistedPublisherNodeDropdownComponent
		);
		fixture.componentRef.setInput('artifactId', '1234');
		fixture.componentRef.setInput('nodes', []);
		fixture.componentRef.setInput('connectionId', '54321');
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
