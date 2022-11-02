/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HighlightFilteredTextDirective } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/highlight-filtered-text.directive';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';

import { SubElementTableNoEditFieldFilteredComponent } from './sub-element-table-no-edit-field-filtered.component';

describe('SubElementTableNoEditFieldFilteredComponent', () => {
	let component: SubElementTableNoEditFieldFilteredComponent;
	let fixture: ComponentFixture<SubElementTableNoEditFieldFilteredComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CommonModule],
			declarations: [
				SubElementTableNoEditFieldFilteredComponent,
				HighlightFilteredTextDirective,
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(
			SubElementTableNoEditFieldFilteredComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
