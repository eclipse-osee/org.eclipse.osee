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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { currentTypesServiceMock } from '../../mocks/services/current.types.service.mock';
import { CurrentTypesService } from '../../services/current-types.service';
import { enumerationSetMock } from '../../mocks/returnObjects/enumerationset.mock'

import { NewTypeDialogComponent } from './new-type-dialog.component';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { MatTableModule } from '@angular/material/table';

describe('NewTypeDialogComponent', () => {
  let component: NewTypeDialogComponent;
  let fixture: ComponentFixture<NewTypeDialogComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatDialogModule, MatStepperModule,NoopAnimationsModule,MatSelectModule,FormsModule,MatFormFieldModule,MatInputModule,MatButtonModule,MatTableModule],
      declarations: [NewTypeDialogComponent],
      providers: [{ provide: MatDialogRef, useValue: {} },
      {provide:CurrentTypesService, useValue:currentTypesServiceMock}]
    })
    .compileComponents();
  });

  beforeEach(async() => {
    fixture = TestBed.createComponent(NewTypeDialogComponent);
    component = fixture.componentInstance;
    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  describe('Page 1 Testing', () => {
    it('should select enumeration', async() => {
      let select = await loader.getHarness(MatSelectHarness);
      await select.clickOptions({ text: 'Enumeration' })
      let button = await loader.getHarness(MatButtonHarness.with({ text: 'Next' }));
      expect(button).toBeDefined();
      expect(await button.isDisabled()).toBe(false);
      await button.click();
    })
  })
  describe('Page 2 testing', () => {
    beforeEach(async () => {
      let selectPage1 = await loader.getHarness(MatSelectHarness);
      await selectPage1.clickOptions({ text: 'Enumeration' })
      let buttonPage1 = await loader.getHarness(MatButtonHarness.with({ text: 'Next' }));
      await buttonPage1.click();
    })
    it('should select an enum set', async () => {
      let selectEnum = await loader.getHarness(MatSelectHarness)
      expect(selectEnum).toBeDefined();
      await selectEnum.open();
      await selectEnum.clickOptions({ text: 'Enumeration' });
      expect(await selectEnum.getValueText()).toEqual('Enumeration');
    })

    it('should toggle enum mode', async() => {
      let button = await loader.getHarness(MatButtonHarness.with({ text: '+' }))
      await button.click();
      let table = await loader.getHarness(MatTableHarness);
      expect(table).toBeDefined();
    })
    describe('enum editing', () => {
      beforeEach(async() => {
        let button = await loader.getHarness(MatButtonHarness.with({ text: '+' }))
        await button.click();
      })
      it('should add an enum', async () => {
        component.enumSet.enumerations = [];
        let table = await loader.getHarness(MatTableHarness);
        expect(table).toBeDefined();
        let addButton = await (await (await (await table.getFooterRows())[0].getCells({ columnName: 'applicability' })))[0].getHarness(MatButtonHarness);
        expect(await addButton.isDisabled()).toBe(false);
        let spy = spyOn(component, 'addEnum').and.callThrough();
        await addButton.click();
        expect(spy).toHaveBeenCalled();
      })
    })
  })
});
