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
import { TestBed } from '@angular/core/testing';
import { TransactionService } from '@osee/shared/transactions';
import { transactionServiceMock } from '@osee/shared/transactions/testing';
import { AttributeService } from 'src/app/ple-services/http/attribute.service';
import { attributeServiceMock } from '../mocks/attribute-service.mock';
import { CurrentTextEditorService } from './current-text-editor.service';

describe('CurrentTextEditorService', () => {
	let service: CurrentTextEditorService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: AttributeService, useValue: attributeServiceMock },
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentTextEditorService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
