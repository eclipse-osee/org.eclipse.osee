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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { XResultData } from '@osee/shared/types';
import { productType, ProductType } from '../types/pl-config-product-types';

@Injectable({
	providedIn: 'root',
})
export class PlConfigTypesService {
	private http = inject(HttpClient);

	getProductTypes(branchId: string) {
		return this.http.get<Required<ProductType>[]>(
			apiURL + `/orcs/branch/${branchId}/applic/product-types`
		);
	}

	createProductType(branchId: string, productType: productType) {
		return this.http.post<XResultData>(
			apiURL + `/orcs/branch/${branchId}/applic/product-types`,
			productType
		);
	}

	updateProductType(branchId: string, productType: productType) {
		return this.http.put<XResultData>(
			apiURL + `/orcs/branch/${branchId}/applic/product-types`,
			productType
		);
	}

	deleteProductType(branchId: string, id: string) {
		return this.http.delete<XResultData>(
			apiURL + `/orcs/branch/${branchId}/applic/product-types/${id}`
		);
	}
}
