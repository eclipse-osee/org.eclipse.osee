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
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService.mock';
import { plConfigTypesServiceMock } from '../../testing/pl-config-types.service.mock';

import { CurrentBranchInfoService } from '@osee/shared/services';
import { testBranchInfo } from '@osee/shared/testing';
import { of } from 'rxjs';
import { ProductTypeDropDownComponent } from './product-type-drop-down.component';

describe('ProductTypeDropDownComponent', () => {
	let component: ProductTypeDropDownComponent;
	let fixture: ComponentFixture<ProductTypeDropDownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ProductTypeDropDownComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
				{
					provide: PlConfigTypesService,
					useValue: plConfigTypesServiceMock,
				},
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ProductTypeDropDownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
