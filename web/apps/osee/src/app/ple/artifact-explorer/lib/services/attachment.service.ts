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
import { inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Attachment } from '../types/team-workflow';
import { Observable } from 'rxjs';

export class AttachmentService {
  private http = inject(HttpClient);
  private baseUrl = '/api/workflows';

  listAttachments(workflowId: string): Observable<Attachment[]> {
    return this.http.get<Attachment[]>(`${this.baseUrl}/${workflowId}/attachments`);
  }

  uploadAttachments(workflowId: string, files: File[]): Observable<Attachment[]> {
    const form = new FormData();
    files.forEach((f) => form.append('files', f));
    return this.http.post<Attachment[]>(`${this.baseUrl}/${workflowId}/attachments`, form);
  }

  updateAttachment(workflowId: string, attachmentId: string, file: File): Observable<Attachment> {
    const form = new FormData();
    form.append('file', file);
    return this.http.put<Attachment>(`${this.baseUrl}/${workflowId}/attachments/${attachmentId}`, form);
  }

  deleteAttachment(workflowId: string, attachmentId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${workflowId}/attachments/${attachmentId}`);
  }

  getDownloadUrl(workflowId: string, attachmentId: string): Observable<{ url: string }> {
    return this.http.get<{ url: string }>(`${this.baseUrl}/${workflowId}/attachments/${attachmentId}/download-url`);
  }

  downloadAttachmentBlob(workflowId: string, attachmentId: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${workflowId}/attachments/${attachmentId}/download`, {
      responseType: 'blob',
    });
  }
}
