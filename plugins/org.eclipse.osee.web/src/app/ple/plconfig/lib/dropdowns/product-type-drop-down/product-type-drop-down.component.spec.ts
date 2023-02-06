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
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService.mock';
import { plConfigTypesServiceMock } from '../../testing/pl-config-types.service.mock';

import { ProductTypeDropDownComponent } from './product-type-drop-down.component';

describe('ProductTypeDropDownComponent', () => {
	let component: ProductTypeDropDownComponent;
	let fixture: ComponentFixture<ProductTypeDropDownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatDialogModule,
				MatMenuModule,
				NoopAnimationsModule,
				MatIconModule,
				MatButtonModule,
			],
			providers: [
				{
					provide: PlConfigTypesService,
					useValue: plConfigTypesServiceMock,
				},
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
			],
			declarations: [ProductTypeDropDownComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(ProductTypeDropDownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
