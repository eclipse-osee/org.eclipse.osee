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
import { ErrorHandler, Injectable, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { UiService } from '@osee/shared/services';

/**
 * Catches errors that would otherwise reach Angular's default handler. Without this, an error
 * thrown during change detection aborts the render pass, which leaves CDK overlays (menus, dialogs)
 * unpositioned at the top-left corner and the UI partially unresponsive. Routing errors here keeps
 * the app rendering; HTTP errors are surfaced through the existing error popup.
 */
@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
	private readonly uiService = inject(UiService);

	handleError(error: unknown): void {
		const unwrapped =
			error && typeof error === 'object' && 'rejection' in error
				? (error as { rejection: unknown }).rejection
				: error;

		if (unwrapped instanceof HttpErrorResponse) {
			this.uiService.httpError = unwrapped;
		}

		console.error(unwrapped);
	}
}
