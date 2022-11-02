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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { applicabilityListServiceMock } from '../../../mocks/ApplicabilityListService.mock';
import { ApplicabilityListService } from '../../../services/http/applicability-list.service';
import { MockEnumFormUnique } from '../enum-form/enum-form.component.mock';

import { EnumSetFormComponent } from './enum-set-form.component';

describe('EnumSetFormComponent', () => {
	let component: EnumSetFormComponent;
	let fixture: ComponentFixture<EnumSetFormComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatTableModule,
				FormsModule,
				MatFormFieldModule,
				MatInputModule,
				MatSelectModule,
				MatIconModule,
				NoopAnimationsModule,
			],
			declarations: [EnumSetFormComponent, MockEnumFormUnique],
			providers: [
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(EnumSetFormComponent);
		component = fixture.componentInstance;
		component.bitSize = '32';
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should update the name', async () => {
		const spy = spyOn(component, 'updateEnumSet').and.callThrough();
		const input = await loader.getHarness(MatInputHarness);
		expect(input).toBeDefined();
		await input.setValue('new name');
		expect(spy).toHaveBeenCalled();
	});
});
