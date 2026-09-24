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
import { HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { OriginIdService } from '@osee/shared/services/network';

/**
 * Stamps the stable per-tab {@code X-Origin-Id} header on every outgoing request.
 *
 * The server echoes this id back on the resulting SSE change event; the originating tab
 * recognizes its own echo and ignores it (self-dedup), so no server-side connection exclusion
 * is needed. Harmless on non-mutating requests — a request that commits nothing produces no
 * echo.
 */
export const OriginIdInterceptor = (
	req: HttpRequest<unknown>,
	next: HttpHandlerFn
) => {
	const originId = inject(OriginIdService).originId;
	return next(
		req.clone({
			setHeaders: { 'X-Origin-Id': originId },
		})
	);
};
