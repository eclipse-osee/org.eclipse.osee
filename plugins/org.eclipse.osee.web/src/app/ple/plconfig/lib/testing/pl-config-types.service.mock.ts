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
import { MockXResultData } from '@osee/shared/testing';
import { of } from 'rxjs';
import { PlConfigTypesService } from '../services/pl-config-types.service';
import {
	productType,
	RequiredProductType,
} from '../types/pl-config-product-types';

export const plConfigTypesServiceMock: Partial<PlConfigTypesService> = {
	getProductTypes(branchId: string) {
		return of([
			new RequiredProductType('Code', '', '1'),
			new RequiredProductType('Test', '', '2'),
		]);
	},
	createProductType(branchId: string, productType: productType) {
		return of(MockXResultData);
	},
	updateProductType(branchId: string, productType: productType) {
		return of(MockXResultData);
	},
	deleteProductType(branchId: string, id: string) {
		return of(MockXResultData);
	},
};
