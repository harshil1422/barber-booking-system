export enum BookingStatus {
  PENDING     = 'PENDING',
  CONFIRMED   = 'CONFIRMED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED   = 'COMPLETED',
  CANCELLED   = 'CANCELLED',
}
 
export enum SlotStatus {
  FREE    = 'FREE',
  BOOKED  = 'BOOKED',
  BUSY    = 'BUSY',
  WALK_IN = 'WALK_IN',
}
 
export interface ChairSlot {
  id: string;
  chairId: string;
  shopId: string;
  bookingId: string | null;
  slotDate: string;      // 'YYYY-MM-DD'
  startsAt: string;      // 'HH:mm'
  endsAt: string;
  status: SlotStatus;
  isWalkIn: boolean;
  version: number;       // @Version from backend — never discard
}
 
export interface Booking {
  id: string;
  userId: string;
  shopId: string;
  chairSlotId: string;
  serviceCatalogId: string;
  status: BookingStatus;
  totalAmount: number;
  idempotencyKey: string;
  cancellationReason: string | null;
  confirmedAt: string | null;
  completedAt: string | null;
  cancelledAt: string | null;
  createdAt: string;
  // UI join fields (populated by frontend from slot/service data)
  startsAt?: string;
  chairName?: string;
  serviceName?: string;
  duration?: number;
}