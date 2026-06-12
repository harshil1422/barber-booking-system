// src/app/core/services/notification.service.ts
import { Injectable, OnDestroy, signal, computed } from '@angular/core';
import { environment }       from '@env/environment';
import { AuthService }       from './auth.service';
import { AppNotification }   from '@core/models/api.models';

@Injectable({ providedIn: 'root' })
export class NotificationService implements OnDestroy {

  private eventSource: EventSource | null = null;

  // Angular Signals for reactive notification state
  private _notifications = signal<AppNotification[]>([]);
  private _connected     = signal(false);

  readonly notifications  = this._notifications.asReadonly();
  readonly unreadCount    = computed(() =>
    this._notifications().filter(n => !n.read).length
  );
  readonly isConnected    = computed(() => this._connected());

  constructor(private authService: AuthService) {
    // Auto-connect when user logs in
    if (this.authService.isLoggedIn()) {
      this.connect();
    }
  }

  // Open SSE connection to Notification service (:8086)
  // Server uses Redis pub/sub → Kafka consumer → SSE push
  connect(): void {
    if (this.eventSource) return;

    const token = this.authService.getAccessToken();
    const url   = `${environment.sseUrl}?token=${token}`;

    this.eventSource = new EventSource(url);

    this.eventSource.onopen = () => {
      this._connected.set(true);
      console.log('[SSE] Connected to notification stream');
    };

    // Handle typed notification events
    this.eventSource.addEventListener('BOOKING_CONFIRMED',    e => this.handleEvent(e));
    this.eventSource.addEventListener('BOOKING_CANCELLED',    e => this.handleEvent(e));
    this.eventSource.addEventListener('APPOINTMENT_REMINDER', e => this.handleEvent(e));
    this.eventSource.addEventListener('PAYMENT_CAPTURED',     e => this.handleEvent(e));
    this.eventSource.addEventListener('REFUND_PROCESSED',     e => this.handleEvent(e));
    this.eventSource.addEventListener('SHOP_VERIFIED',        e => this.handleEvent(e));
    this.eventSource.addEventListener('REVIEW_RECEIVED',      e => this.handleEvent(e));

    this.eventSource.onerror = () => {
      this._connected.set(false);
      this.eventSource?.close();
      this.eventSource = null;
      // Reconnect after 5 seconds
      setTimeout(() => this.connect(), 5000);
    };
  }

  markAsRead(notificationId: string): void {
    this._notifications.update(list =>
      list.map(n => n.id === notificationId ? { ...n, read: true } : n)
    );
  }

  markAllAsRead(): void {
    this._notifications.update(list => list.map(n => ({ ...n, read: true })));
  }

  disconnect(): void {
    this.eventSource?.close();
    this.eventSource = null;
    this._connected.set(false);
  }

  private handleEvent(event: MessageEvent): void {
    try {
      const notification: AppNotification = JSON.parse(event.data);
      this._notifications.update(list => [notification, ...list]);
    } catch {
      console.error('[SSE] Failed to parse notification event', event.data);
    }
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
