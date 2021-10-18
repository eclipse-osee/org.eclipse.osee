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
import { TestBed } from '@angular/core/testing';
import { userDataAccountServiceMock } from 'src/app/ple/plconfig/testing/mockUserDataAccountService';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { MimPreferencesServiceMock } from '../../mocks/MimPreferencesService.mock';
import { MimPreferencesService } from '../http/mim-preferences.service';

import { PreferencesUIService } from './preferences-ui.service';

describe('PreferencesUIService', () => {
  let service: PreferencesUIService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
      {provide: UserDataAccountService, useValue:userDataAccountServiceMock}]
    });
    service = TestBed.inject(PreferencesUIService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
