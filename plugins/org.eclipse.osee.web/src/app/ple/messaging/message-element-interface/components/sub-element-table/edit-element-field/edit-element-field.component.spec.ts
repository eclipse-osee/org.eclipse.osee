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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatAutocompleteHarness } from '@angular/material/autocomplete/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { SharedMessagingModule } from 'src/app/ple/messaging/shared/shared-messaging.module';
import { CurrentStateServiceMock } from '../../../mocks/services/CurrentStateService.mock';
import { CurrentStateService } from '../../../services/current-state.service';

import { EditElementFieldComponent } from './edit-element-field.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatMenuModule } from '@angular/material/menu';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatMenuHarness, MatMenuItemHarness } from '@angular/material/menu/testing';

describe('EditElementFieldComponent', () => {
  let component: EditElementFieldComponent;
  let fixture: ComponentFixture<EditElementFieldComponent>;
  let loader: HarnessLoader;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate','serializeUrl','createUrlTree']);
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, MatAutocompleteModule, FormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, SharedMessagingModule,MatMenuModule,RouterTestingModule],
      providers: [{ provide: CurrentStateService, useValue: CurrentStateServiceMock },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: {} }],
      declarations: [ EditElementFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(function () {
    let window1 = spyOn(window, 'open').and.callFake((url,target,replace) => {
      return null;
    })
  });
  beforeEach(() => {
    fixture = TestBed.createComponent(EditElementFieldComponent);
    component = fixture.componentInstance;
    component.structureId = '10';
    component.elementId = '15';
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

    it('should update the applicability', fakeAsync(async() => {
      let spy = spyOn(component, 'updateElement').and.callThrough();
      let select = await loader.getHarness(MatSelectHarness);
      await select.open();
      if (await select.isOpen()) {
        await select.clickOptions({ text: 'Second' });
        expect(spy).toHaveBeenCalled()
      } else {
        expect(spy).not.toHaveBeenCalled()
      }

    }))

  })

  describe('Platform Type Editing', () => {
    beforeEach(() => {
      component.header='platformTypeName2'
      component.value = 'First'
    })

    it('should update the platform type', fakeAsync(async() => {
      let select = await loader.getHarness(MatAutocompleteHarness);
      await select.focus();
      await select.isOpen() 
      await select.enterText('2');
      tick(500);
      expect(await select.getValue()).toBe('First2')
    }))

    it('should navigate to types page', async () => {
      let spy = spyOn(component, 'navigateTo').and.callThrough();
      let button = await loader.getHarness(MatButtonHarness.with({ text: '->' }))
      await button.click();
      expect(spy).toHaveBeenCalled();
      expect(routerSpy.navigate).toHaveBeenCalled();
    })

    it('should emit an event to parent component', () => {
      let mEvent = document.createEvent("MouseEvent");
      let spy = spyOn(component.contextMenu, 'emit');
      component.openMenu(mEvent, '');
      expect(spy).toHaveBeenCalledWith(mEvent);
    })
  })
});
