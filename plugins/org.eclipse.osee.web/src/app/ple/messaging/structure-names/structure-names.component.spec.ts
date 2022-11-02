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
import { FormsModule } from '@angular/forms';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { BranchPickerModule } from '../../../shared-components/components/branch-picker/branch-picker.module';
import { BranchPickerStub } from '../../../shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';
import { structuresNameServiceMock } from '../shared/mocks/structuresNamesService.mock';
import { StructureNamesService } from '../shared/services/http/structure-names.service';

import { StructureNamesComponent } from './structure-names.component';

describe('StructureNamesComponent', () => {
	let component: StructureNamesComponent;
	let fixture: ComponentFixture<StructureNamesComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				RouterTestingModule,
				MatExpansionModule,
				MatFormFieldModule,
				FormsModule,
				MatIconModule,
				MatInputModule,
				NoopAnimationsModule,
			],
			providers: [
				{
					provide: StructureNamesService,
					useValue: structuresNameServiceMock,
				},
			],
			declarations: [StructureNamesComponent, BranchPickerStub],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(StructureNamesComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
