/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { RouterTestingModule } from '@angular/router/testing';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import {
	TypesService,
	MimPreferencesService,
	EnumerationSetService,
	EnumsService,
	ApplicabilityListService,
} from '@osee/messaging/shared/services';
import {
	typesServiceMock,
	MimPreferencesServiceMock,
	enumerationSetServiceMock,
	enumsServiceMock,
	applicabilityListServiceMock,
} from '@osee/messaging/shared/testing';
import { TransactionBuilderService } from '@osee/shared/transactions';
import { transactionBuilderMock } from '@osee/shared/transactions/testing';

import { TypeDetailComponent } from './type-detail.component';

describe('TypeDetailComponent', () => {
	let component: TypeDetailComponent;
	let fixture: ComponentFixture<TypeDetailComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [RouterTestingModule, TypeDetailComponent],
			providers: [
				{ provide: TypesService, useValue: typesServiceMock },
				{
					provide: MimPreferencesService,
					useValue: MimPreferencesServiceMock,
				},
				{
					provide: EnumerationSetService,
					useValue: enumerationSetServiceMock,
				},
				{
					provide: TransactionBuilderService,
					useValue: transactionBuilderMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{ provide: EnumsService, useValue: enumsServiceMock },
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TypeDetailComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
