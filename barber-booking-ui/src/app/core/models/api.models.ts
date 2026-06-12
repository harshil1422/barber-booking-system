// ── Spring Boot error shape ───────────────────────────────────────────────
// Matches your GlobalExceptionHandler ErrorResponse record exactly.
// Used in error.interceptor.ts to extract the human-readable message.

export interface ApiError {
  status:    number;
  error:     string;   // e.g. "Unauthorized"
  message:   string;   // human-readable — shown in snackbar
  path:      string;   // e.g. "/api/v1/auth/login"
  timestamp: string;   // ISO-8601
}

// ── Generic paginated response ────────────────────────────────────────────
// Used by booking and admin features when listing resources.

export interface PageResponse<T> {
  content:       T[];
  totalElements: number;
  totalPages:    number;
  size:          number;
  number:        number;
  first:         boolean;
  last:          boolean;
}

// ── NOTIFICATION ─────────────────────────────────────────────
export type NotificationType =
  | 'BOOKING_CONFIRMED'
  | 'BOOKING_CANCELLED'
  | 'APPOINTMENT_REMINDER'
  | 'REVIEW_RECEIVED'
  | 'PAYMENT_CAPTURED'
  | 'REFUND_PROCESSED'
  | 'SHOP_VERIFIED';

export interface AppNotification {
  id:        string;
  type:      NotificationType;
  title:     string;
  message:   string;
  read:      boolean;
  createdAt: string;
  actionUrl?: string;
}