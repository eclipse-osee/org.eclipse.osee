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
import { MatButtonHarness } from '@angular/material/button/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MessagingComponent } from './messaging.component';
import { MessagingHelpDummy, MessagingMainMock, MessagingTypeSearchMock } from './mocks/components/navigation-components.mock';

describe('MessagingComponent', () => {
  let component: MessagingComponent;
  let fixture: ComponentFixture<MessagingComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[RouterTestingModule.withRoutes([{path:'connections',component:MessagingMainMock},{path:'typeSearch',component:MessagingTypeSearchMock},{path:'help',component:MessagingHelpDummy}])],
      declarations: [ MessagingComponent,MessagingMainMock,MessagingTypeSearchMock,MessagingHelpDummy ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessagingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('testing navigation', ()=>{

    it('should navigate to the type search page', async () => {
      const spy = spyOn(component, 'navigateTo').and.callThrough();
      const button = await loader.getHarness(MatButtonHarness.with({ text: 'Find elements by type' }));
      await button.click();
      expect(spy).toHaveBeenCalledWith('typeSearch')
    })

    it('should navigate to the main mim tool', async () => {
      const spy = spyOn(component, 'navigateTo').and.callThrough();
      const button = await loader.getHarness(MatButtonHarness.with({ text: 'Go to Connection View' }));
      await button.click();
      expect(spy).toHaveBeenCalledWith('connections')
    })

    it('should navigate to the mim help pages', async () => {
      const spy = spyOn(component, 'navigateTo').and.callThrough();
      const button = await loader.getHarness(MatButtonHarness.with({ text: 'Go To Help Pages' }));
      await button.click();
      expect(spy).toHaveBeenCalledWith('help')
    })
  })
});
