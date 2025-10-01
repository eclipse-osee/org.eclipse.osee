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
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { WorkflowAttachment } from '../types/team-workflow';
import { apiURL } from '@osee/environments';

@Injectable({ providedIn: 'root' })
export class AttachmentTestingService {
  private readonly latencyMs = 150;
  private store = new Map<string, WorkflowAttachment[]>();

  //////////////

  private http = inject(HttpClient);
  private teamWfBasePath = '/ats/teamwf';

  listAttachments(workflowId: string): Observable<WorkflowAttachment[]> {
    return this.http.get<WorkflowAttachment[]>(apiURL + `${this.teamWfBasePath}/${workflowId}/attachments`);
  }

  /////////////

  // listAttachments(workflowId: string): Observable<WorkflowAttachment[]> {
  //   const list = this.ensureSeeded(workflowId);
  //   return of(this.clone(list)).pipe(delay(this.latencyMs));
  // }

  uploadAttachments(workflowId: string, files: File[]): Observable<WorkflowAttachment[]> {
    const now = new Date().toISOString();
    const current = this.ensureSeeded(workflowId);
    const created: WorkflowAttachment[] = files.map((f, i) => ({
      id: `new-${workflowId}-${Date.now()}-${i}`,
      name: f.name,
      extension: (f.name.split('.').pop() || '').toLowerCase(),
      sizeInBytes: f.size,
      // For testing we typically omit attachmentBytes in list responses
      attachmentBytes: undefined,
    }));
    this.store.set(workflowId, [...current, ...created]);
    return of(this.clone(created)).pipe(delay(this.latencyMs));
  }

  updateAttachment(workflowId: string, attachmentId: string, file: File): Observable<WorkflowAttachment> {
    const list = this.ensureSeeded(workflowId);
    const idx = list.findIndex((a) => a.id === attachmentId);

    const updated: WorkflowAttachment = {
      ...(idx >= 0 ? list[idx] : { id: attachmentId, name: '', extension: '', sizeInBytes: 0 }),
      name: file.name,
      extension: (file.name.split('.').pop() || '').toLowerCase(),
      sizeInBytes: file.size,
      attachmentBytes: undefined,
    };

    if (idx >= 0) {
      const next = [...list];
      next[idx] = updated;
      this.store.set(workflowId, next);
    } else {
      this.store.set(workflowId, [...list, updated]);
    }

    return of(structuredClone(updated)).pipe(delay(this.latencyMs));
  }

  deleteAttachment(workflowId: string, attachmentId: string): Observable<void> {
    const list = this.ensureSeeded(workflowId);
    this.store.set(workflowId, list.filter((a) => a.id !== attachmentId));
    return of(undefined).pipe(delay(this.latencyMs));
  }

  getDownloadUrl(workflowId: string, attachmentId: string): Observable<{ url: string }> {
    const url = `about:blank#${encodeURIComponent(workflowId)}-${encodeURIComponent(attachmentId)}`;
    return of({ url }).pipe(delay(this.latencyMs));
  }

  // Simulate a single-item fetch with bytes populated
  getAttachment(workflowId: string, attachmentId: string): Observable<WorkflowAttachment> {
    const content = `Mock content for ${attachmentId} (workflow ${workflowId})`;
    const base64 = btoa(content);
    const list = this.ensureSeeded(workflowId);
    const found = list.find((a) => a.id === attachmentId) ?? {
      id: attachmentId,
      name: `mock-${attachmentId}.txt`,
      extension: 'txt',
      sizeInBytes: content.length,
    };
    const withBytes: WorkflowAttachment = { ...found, attachmentBytes: base64 };
    return of(structuredClone(withBytes)).pipe(delay(this.latencyMs));
  }

  private ensureSeeded(workflowId: string): WorkflowAttachment[] {
    if (!this.store.has(workflowId)) {
      const seeded: WorkflowAttachment[] = [
        {
          id: `att-${workflowId}-001`,
          name: 'requirements.pdf',
          extension: 'pdf',
          sizeInBytes: 123456,
        },
        {
          id: `att-${workflowId}-002`,
          name: 'screenshot.png',
          extension: 'png',
          sizeInBytes: 98765,
        },
      ];
      this.store.set(workflowId, seeded);
    }
    return this.store.get(workflowId) as WorkflowAttachment[];
  }

  private clone<T>(arr: T[]): T[] {
    try {
      return structuredClone(arr);
    } catch {
      return JSON.parse(JSON.stringify(arr)) as T[];
    }
  }
}
