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
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatAutocompleteHarness } from '@angular/material/autocomplete/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { enumsServiceMock } from 'src/app/ple/messaging/shared/mocks/EnumsService.mock';
import { unitsMock } from 'src/app/ple/messaging/shared/mocks/unit.mock';
import { EnumsService } from 'src/app/ple/messaging/shared/services/http/enums.service';
import { SharedMessagingModule } from 'src/app/ple/messaging/shared/shared-messaging.module';
import { warningDialogServiceMock } from '../../../../shared/mocks/warning-dialog.ui.service.mock';
import { WarningDialogService } from '../../../../shared/services/ui/warning-dialog.service';
import { CurrentStateServiceMock } from '../../../mocks/services/CurrentStateService.mock';
import { CurrentStructureService } from '../../../services/current-structure.service';
import { EditElementFieldComponent } from './edit-element-field.component';


describe('EditElementFieldComponent', () => {
  let component: EditElementFieldComponent;
  let fixture: ComponentFixture<EditElementFieldComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule,MatIconModule, MatAutocompleteModule, FormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, SharedMessagingModule,MatMenuModule,RouterTestingModule],
      providers: [{ provide: CurrentStructureService, useValue: CurrentStateServiceMock },
        { provide: EnumsService, useValue:enumsServiceMock },
        { provide: ActivatedRoute, useValue: {} },
        { provide: WarningDialogService, useValue: warningDialogServiceMock }],
      declarations: [ EditElementFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditElementFieldComponent);
    component = fixture.componentInstance;
    component.structureId = '10';
    component.elementId = '15';
    component.platformTypeId='20'
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Applicability Editing', () => {
    beforeEach(() => {
      component.header='applicability'
      component.value = { id: '1', name: 'Base' }
      fixture.detectChanges();
      loader = TestbedHarnessEnvironment.loader(fixture);
    })

    it('should update the applicability', async() => {
      let spy = spyOn(component, 'updateImmediately').and.callThrough();
      let select = await loader.getHarness(MatSelectHarness);
      await select.open();
      if (await select.isOpen()) {
        await select.clickOptions({ text: 'Second' });
        component.focusChanged(null);
        expect(spy).toHaveBeenCalled()
      } else {
        expect(spy).not.toHaveBeenCalled()
      }
    })

    it('should update value', fakeAsync(() => {
      let spy = spyOn(component, 'updateElement').and.callThrough();
      component.focusChanged('mouse');
      component.focusChanged('mouse');
      component.focusChanged(null)
      component.updateElement('description', 'v2');
      tick(500);
      expect(spy).toHaveBeenCalled();
    }));

  })

  describe('Platform Type Editing', () => {
    beforeEach(() => {
      component.header='platformTypeName2'
      component.value = 'First'
    })

    it('should update the platform type', fakeAsync(async () => {
      component.focusChanged('mouse');
      component.focusChanged('mouse');
      component.focusChanged(null);
      let select = await loader.getHarness(MatAutocompleteHarness);
      await select.focus();
      await select.isOpen() 
      await select.enterText('2');
      tick(500);
      expect(await select.getValue()).toBe('First2')
    }))

    it('should emit an event to parent component', () => {
      let mEvent = document.createEvent("MouseEvent");
      let spy = spyOn(component.contextMenu, 'emit');
      component.openMenu(mEvent, '');
      expect(spy).toHaveBeenCalledWith(mEvent);
    })
  })
  describe('Units editing', () => {
    beforeEach(() => {
      component.header='units'
      component.value = unitsMock[0]
    })
    it('should update the units', fakeAsync(async () => {
      component.focusChanged('mouse');
      component.focusChanged('mouse');
      component.focusChanged(null);
      let select = await loader.getHarness(MatSelectHarness.with({selector:'.unit-selector'}));
      await select.focus();
      await select.open();
      await select.isOpen();
      await select.clickOptions({ text: unitsMock[5] });
      tick(500);
      expect(await select.getValueText()).toBe(unitsMock[5])
    }))
  })
});
