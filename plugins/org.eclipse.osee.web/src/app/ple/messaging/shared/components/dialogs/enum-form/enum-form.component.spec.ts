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
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTableHarness } from '@angular/material/table/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { applicabilityListServiceMock } from '../../../mocks/ApplicabilityListService.mock';
import { QueryServiceMock } from '../../../mocks/query.service.mock';
import { ApplicabilityListService } from '../../../services/http/applicability-list.service';
import { QueryService } from '../../../services/http/query.service';
import { take } from 'rxjs/operators';

import { EnumFormComponent } from './enum-form.component';
import { MatOptionLoadingModule } from '../../../../../../shared-components/mat-option-loading/mat-option-loading.module';

describe('EnumFormComponent', () => {
  let component: EnumFormComponent;
  let fixture: ComponentFixture<EnumFormComponent>;
  let loader: HarnessLoader;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                MatTableModule,
                FormsModule,
                MatFormFieldModule,
                MatInputModule,
                MatSelectModule,
                NoopAnimationsModule,
                MatOptionLoadingModule
            ],
            declarations: [EnumFormComponent],
            providers: [
                {
                    provide: ApplicabilityListService,
                    useValue: applicabilityListServiceMock,
                },
                { provide: QueryService, useValue: QueryServiceMock },
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(EnumFormComponent);
      component = fixture.componentInstance;
      component.enumSetName = 'enumSet1';
      component.bitSize = '32';
      loader = TestbedHarnessEnvironment.loader(fixture);
      fixture.detectChanges();
      component.ngOnChanges({});
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
  
  it('should add an enum', async () => {
        let table = await loader.getHarness(MatTableHarness);
        expect(table).toBeDefined();
        let addButton = await (await (await (await table.getFooterRows())[0].getCells({ columnName: 'applicability' })))[0].getHarness(MatButtonHarness);
        expect(await addButton.isDisabled()).toBe(false);
        let spy = spyOn(component, 'addEnum').and.callThrough();
        await addButton.click();
        expect(spy).toHaveBeenCalled();
  })
});
