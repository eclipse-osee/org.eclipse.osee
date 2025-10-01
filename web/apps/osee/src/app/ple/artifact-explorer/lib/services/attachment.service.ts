/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WorkflowAttachment } from '../types/team-workflow';
import { apiURL } from '@osee/environments';

@Injectable({ providedIn: 'root' })
export class AttachmentService {
  private http = inject(HttpClient);
  private teamWfBasePath = '/ats/teamwf';

  listAttachments(workflowId: string): Observable<WorkflowAttachment[]> {
    return this.http.get<WorkflowAttachment[]>(apiURL + `${this.teamWfBasePath}/${workflowId}/attachments`);
  }

  uploadAttachments(workflowId: string, files: File[]): Observable<WorkflowAttachment[]> {
    const form = new FormData();
    files.forEach((f) => form.append('files', f));
    return this.http.post<WorkflowAttachment[]>(`${this.teamWfBasePath}/${workflowId}/attachments`, form);
  }

  updateAttachment(workflowId: string, attachmentId: string, file: File): Observable<WorkflowAttachment> {
    const form = new FormData();
    form.append('file', file);
    return this.http.put<WorkflowAttachment>(`${this.teamWfBasePath}/${workflowId}/attachments/${attachmentId}`, form);
  }

  deleteAttachment(workflowId: string, attachmentId: string): Observable<void> {
    return this.http.delete<void>(`${this.teamWfBasePath}/${workflowId}/attachments/${attachmentId}`);
  }

  // If your backend provides a URL endpoint for direct open:
  getDownloadUrl(workflowId: string, attachmentId: string): Observable<{ url: string }> {
    return this.http.get<{ url: string }>(
      `${this.teamWfBasePath}/${workflowId}/attachments/${attachmentId}/download-url`
    );
  }

  // If you have a dedicated endpoint that returns a single WorkflowAttachment with attachmentBytes:
  getAttachment(workflowId: string, attachmentId: string): Observable<WorkflowAttachment> {
    return this.http.get<WorkflowAttachment>(`${this.teamWfBasePath}/${workflowId}/attachments/${attachmentId}`);
  }
}
