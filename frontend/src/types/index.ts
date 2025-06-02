// src/types/index.ts
export interface User {
  id: number;
  username: string;
  role: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  refreshType: string; // "Refresh Token"
}

export interface ApiErrorResponse {
  message: string;
  // Add other potential error fields from your backend
}

export interface CreateTransactionPayload {
  name: string;
  price: number;
  description?: string;
  time: string; // ISO 8601 format: "2025-05-12T14:45:00"
  categoryName: string; // e.g., "HEALTHCARE"
}

export interface Transaction {
  id: number;
  transactionDate: string; // ISO 8601 format
  price: number;
  shortName: string;
  description: string | null;
  categoryType: 'INCOME' | 'EXPENSES';
  categoryName: string;
}

export interface TransactionsResponse {
  infoLedgerDTO: Transaction[];
  pageNo: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ChangeTransactionPayload {
    id: number;
    transactionDate?: string; // ISO 8601 format
    price?: number;
    description?: string;
    shortName?: string;
    category?: string; // Category Name
}


export interface CategoryCircleItem {
  categoryName: string;
  sum: number;
  categoryType: 'INCOME' | 'EXPENSES';
}

export interface MonthStatsResponse {
  totalIncomeForMonth: number;
  totalExpensesForMonth: number;
  biggestIncome: number;
  biggestExpenses: number;
  difference: number;
  incomeChange: number; // in percent
  expensesChange: number; // in percent
  categoryCircle: CategoryCircleItem[];
}

// --- Category Types ---
export interface Category {
  id: number;
  name: string;
  categoryType: 'INCOME' | 'EXPENSES';
}

// --- Portfolio Types ---
export interface PortfolioItem {
  cryptoName: string;
  amount: number;
  currentPrice: number;
  last_update: string; // ISO 8601 format "2025-05-28T11:46:32.845"
}

export interface BuyCryptoPayload {
  cryptoId: string; // e.g., "bitcoin"
  dateTime: string; // ISO 8601 format
  amount: number;
}

export interface SellCryptoPayload {
  cryptoId: string; // e.g., "bitcoin"
  dateTime: string; // ISO 8601 format
  amount: number;
}


// --- Cryptocurrency Types ---
export interface CryptoListItem {
  id: string; // "bitcoin"
  symbol: string; // "btc"
  name: string; // "Bitcoin"
  current_price: number;
  last_updated: string; // ISO 8601 format
  price_change_24h: number;
  price_change_percentage_24h: number;
  image: string; // URL
  include_platform?: null; // Or whatever this type could be
}

export interface CryptoListResponse {
  content: CryptoListItem[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: false;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page number
  sort: { // same as pageable.sort
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface CryptoDailyPrice {
  price: number;
  dateTime: string; // ISO 8601 format
}