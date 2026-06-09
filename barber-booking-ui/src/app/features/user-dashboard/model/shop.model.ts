export enum ApprovalStatus {
  PENDING_REVIEW = 'PENDING_REVIEW',
  VERIFIED       = 'VERIFIED',
  SUSPENDED      = 'SUSPENDED',
}
 
export interface ServiceCatalog {
  id: string;
  shopId: string;
  name: string;
  description: string;
  durationMinutes: number;
  price: number;
  isActive: boolean;
}
 
export interface ShopHour {
  id: string;
  shopId: string;
  dayOfWeek: number;  // 0=Mon … 6=Sun
  opensAt: string;    // 'HH:mm'
  closesAt: string;
  isClosed: boolean;
}
 
export interface Shop {
  id: string;
  ownerId: string;
  name: string;
  description: string;
  address: string;
  city: string;
  state: string;
  pincode: string;
  approvalStatus: ApprovalStatus;
  cancellationPolicy: string;
  cancellationMinutes: number;
  isOpen: boolean;
  avgRating: number;
  totalReviews: number;
  tags: string[];
  services: ServiceCatalog[];
  hours: ShopHour[];
  createdAt: string;
  // UI-only fields from Discovery service
  distanceKm?: number;
  nextAvailableSlot?: string;
  walkInWaitMinutes?: number;
}