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
import { CiSetsTableComponent } from './ci-sets-table.component';
import { CiSetsService } from '../../../services/ci-sets.service';
import { ciSetServiceMock } from '@osee/ci-dashboard/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton, MatMiniFabButton } from '@angular/material/button';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MockPersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input/testing';
import { MockPersistedBooleanAttributeToggleComponent } from '@osee/attributes/persisted-boolean-attribute-toggle/testing';
import { FormsModule } from '@angular/forms';

describe('CiSetsTableComponent', () => {
	let component: CiSetsTableComponent;
	let fixture: ComponentFixture<CiSetsTableComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(CiSetsTableComponent, {
			set: {
				imports: [
					FormsModule,
					MatTable,
					MatColumnDef,
					MatCell,
					MatCellDef,
					MatRow,
					MatRowDef,
					MatHeaderCell,
					MatHeaderCellDef,
					MatHeaderRow,
					MatHeaderRowDef,
					MatTooltip,
					MatIcon,
					MatIconButton,
					MatMiniFabButton,
					MatFormField,
					MatInput,
					MockPersistedStringAttributeInputComponent,
					MockPersistedBooleanAttributeToggleComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [CiSetsTableComponent],
				providers: [
					provideNoopAnimations(),
					{ provide: CiSetsService, useValue: ciSetServiceMock },
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(CiSetsTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
