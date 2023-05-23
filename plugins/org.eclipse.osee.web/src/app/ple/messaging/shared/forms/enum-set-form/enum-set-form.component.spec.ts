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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectModule } from '@angular/material/select';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ApplicabilityListUIService } from '@osee/shared/services';
import {
	MockEnumFormUniqueComponent,
	MockEnumSetUniqueDescriptionDirective,
} from '@osee/messaging/shared/testing';

import { EnumSetFormComponent } from './enum-set-form.component';
import { applicabilityListUIServiceMock } from '@osee/shared/testing';
import { MockApplicabilitySelectorComponent } from '@osee/shared/components/testing';

@Component({
	selector: 'osee-test-standalone-form',
	standalone: true,
	imports: [FormsModule, EnumSetFormComponent],
	template:
		'<form #testForm="ngForm"><osee-enum-set-form [bitSize]="32"></osee-enum-set-form></form>',
})
class ParentDriverComponent implements AfterViewInit {
	ngAfterViewInit(): void {
		const val = 0; //do nothing
	}
	@ViewChild(EnumSetFormComponent) enumsetForm!: EnumSetFormComponent;
}

describe('EnumSetFormComponent', () => {
	let component: EnumSetFormComponent;
	let fixture: ComponentFixture<ParentDriverComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				NoopAnimationsModule,
				FormsModule,
				ParentDriverComponent,
				EnumSetFormComponent,
			],
		})
			.overrideComponent(EnumSetFormComponent, {
				set: {
					imports: [
						MatFormFieldModule,
						FormsModule,
						MatInputModule,
						MatButtonModule,
						MatIconModule,
						MatSelectModule,
						MatOptionModule,
						MockEnumFormUniqueComponent,
						AsyncPipe,
						NgFor,
						NgIf,
						MockEnumSetUniqueDescriptionDirective,
						MockApplicabilitySelectorComponent,
					],
					providers: [
						{
							provide: ApplicabilityListUIService,
							useValue: applicabilityListUIServiceMock,
						},
					],
				},
			})
			.compileComponents();

		fixture = TestBed.createComponent(ParentDriverComponent);
		component = fixture.debugElement.query(
			By.css('osee-enum-set-form')
		).componentInstance;
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
