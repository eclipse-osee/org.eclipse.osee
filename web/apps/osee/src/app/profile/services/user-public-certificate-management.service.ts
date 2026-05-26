/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { inject, Injectable } from '@angular/core';
import {
	HttpClient,
	HttpHeaders,
	HttpResponse,
	httpResource,
	HttpResourceRef,
} from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { apiURL } from '@osee/environments';
import { UiService } from '@osee/shared/services';

export type UploadPublicCertificateRequest = {
	readonly certificatePem: string;
};

@Injectable({ providedIn: 'root' })
export class UserPublicCertificateManagementService {
	private readonly http = inject(HttpClient);
	private readonly uiService = inject(UiService);

	getPublicCertificateResource(): HttpResourceRef<string | undefined> {
		return httpResource.text(() => {
			this.uiService.updateCount();

			return apiURL + `/orcs/user/public-certificate`;
		});
	}

	downloadPublicCertificate(): Promise<HttpResponse<string>> {
		return firstValueFrom(
			this.http.get(apiURL + `/orcs/user/public-certificate`, {
				responseType: 'text',
				observe: 'response',
			})
		);
	}

	async uploadPublicCertificate(
		request: UploadPublicCertificateRequest
	): Promise<void> {
		const headers = new HttpHeaders({
			'Content-Type': 'text/plain; charset=utf-8',
		});

		await firstValueFrom(
			this.http.put<void>(
				apiURL + `/orcs/user/public-certificate`,
				request.certificatePem,
				{ headers }
			)
		);

		this.uiService.updated = true;
	}

	async deletePublicCertificate(): Promise<void> {
		await firstValueFrom(
			this.http.delete<void>(apiURL + `/orcs/user/public-certificate`)
		);

		this.uiService.updated = true;
	}
}
