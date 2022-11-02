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
import { enumsServiceMock } from '../../../mocks/EnumsService.mock';
import { QueryServiceMock } from '../../../mocks/query.service.mock';
import { typesServiceMock } from '../../../mocks/types.service.mock';
import { QueryService } from '../../../services/http/query.service';
import { TypesService } from '../../../services/http/types.service';
import { EnumsService } from '../../../services/http/enums.service';

import { NewTypeFormComponent } from './new-type-form.component';
import { NewAttributeFormFieldComponent } from '../new-attribute-form-field/new-attribute-form-field.component';

describe('NewTypeFormComponent', () => {
	let component: NewTypeFormComponent;
	let fixture: ComponentFixture<NewTypeFormComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			providers: [
				{ provide: QueryService, useValue: QueryServiceMock },
				{ provide: TypesService, useValue: typesServiceMock },
				{ provide: EnumsService, useValue: enumsServiceMock },
			],
			declarations: [
				NewTypeFormComponent,
				NewAttributeFormFieldComponent,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(NewTypeFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
