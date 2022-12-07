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
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatTableModule } from '@angular/material/table';
import { MatTableHarness } from '@angular/material/table/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatOptionLoadingModule } from '../../../../../shared-components/mat-option-loading/mat-option-loading.module';
import { UserDataAccountService } from '../../../../../userdata/services/user-data-account.service';
import { userDataAccountServiceMock } from '../../../../../userdata/services/user-data-account.service.mock';
import { applicabilityListServiceMock } from '../../mocks/ApplicabilityListService.mock';
import { enumerationSetServiceMock } from '../../mocks/enumeration.set.service.mock';
import { MimPreferencesServiceMock } from '../../mocks/MimPreferencesService.mock';
import { platformTypes1 } from '../../mocks/PlatformTypes.mock';
import { typesServiceMock } from '../../mocks/types.service.mock';
import { ApplicabilityListService } from '../../services/http/applicability-list.service';
import { EnumerationSetService } from '../../services/http/enumeration-set.service';
import { MimPreferencesService } from '../../services/http/mim-preferences.service';
import { TypesService } from '../../services/http/types.service';
import { MockEnumFormUnique } from '../dialogs/enum-form/enum-form.component.mock';

import { EditEnumSetFieldComponent } from './edit-enum-set-field.component';

describe('EditEnumSetFieldComponent', () => {
	let component: EditEnumSetFieldComponent;
	let fixture: ComponentFixture<EditEnumSetFieldComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [EditEnumSetFieldComponent, MockEnumFormUnique],
			imports: [
				MatIconModule,
				MatSelectModule,
				MatInputModule,
				MatFormFieldModule,
				FormsModule,
				MatTableModule,
				NoopAnimationsModule,
				MatOptionLoadingModule,
			],
			providers: [
				{
					provide: EnumerationSetService,
					useValue: enumerationSetServiceMock,
				},
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
				{
					provide: MimPreferencesService,
					useValue: MimPreferencesServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{ provide: TypesService, useValue: typesServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(EditEnumSetFieldComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
	});
	describe('Case 1 Platform Type By Id', () => {
		beforeEach(() => {
			component.editable = true;
			component.platformTypeId = '10';
			fixture.detectChanges();
		});
		it('should create', () => {
			expect(component).toBeTruthy();
		});
	});
	describe('Case 2 Platform Type by Type', () => {
		beforeEach(() => {
			component.editable = true;
			component.platformType = platformTypes1[0];
			fixture.detectChanges();
		});

		it('should select an applicability', async () => {
			const spy = spyOn(component, 'setApplicability').and.callThrough();
			const select = await loader.getHarness(MatSelectHarness);
			await select.open();
			const option = await select.getOptions({ text: 'Second' });
			await option?.[0].click();
			expect(spy).toHaveBeenCalled();
		});

		it('should create', () => {
			expect(component).toBeTruthy();
		});
	});
});
