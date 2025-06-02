import React, { createContext, useState, useEffect, useCallback } from 'react';
import type {ReactNode} from 'react';
import type { AuthResponse, User } from '../types';
import api from '../api/apiService'; 
import { jwtDecode } from 'jwt-decode'; 

interface DecodedToken {
  sub: string; 
  id: number;
  role: string;
  iat: number;
  exp: number;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  accessToken: string | null;
  isLoading: boolean;
  login: (usernameInput: string, passwordInput: string) => Promise<void>;
  register: (usernameInput: string, passwordInput: string) => Promise<string>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [accessToken, setAccessToken] = useState<string | null>(localStorage.getItem('accessToken'));
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true); // To check initial auth state

  const decodeAndSetUser = useCallback((token: string | null) => {
    if (token) {
      try {
        const decodedToken = jwtDecode<DecodedToken>(token);
        // Check if token is expired
        if (decodedToken.exp * 1000 < Date.now()) {
          console.log("Token expired on load");
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          setAccessToken(null);
          setUser(null);
          return null;
        }
        setUser({ id: decodedToken.id, username: decodedToken.sub, role: decodedToken.role });
        return { id: decodedToken.id, username: decodedToken.sub, role: decodedToken.role };
      } catch (error) {
        console.error("Failed to decode token:", error);
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setAccessToken(null);
        setUser(null);
        return null;
      }
    }
    setUser(null);
    return null;
  }, []);


  useEffect(() => {
    setIsLoading(true);
    const token = localStorage.getItem('accessToken');
    if (token) {
      setAccessToken(token);
      decodeAndSetUser(token);
    }
    setIsLoading(false);
  }, [decodeAndSetUser]);

  const login = async (usernameInput: string, passwordInput: string) => {
    const response = await api.post<AuthResponse>('/auth/login', {
      username: usernameInput,
      password: passwordInput,
    });
    const { accessToken: newAccessToken, refreshToken } = response.data;
    localStorage.setItem('accessToken', newAccessToken);
    localStorage.setItem('refreshToken', refreshToken);
    setAccessToken(newAccessToken);
    decodeAndSetUser(newAccessToken);
  };

  const register = async (usernameInput: string, passwordInput: string) => {
    const response = await api.post<string>( // Expecting string "User registered successfully!"
      '/auth/register',
      { username: usernameInput, password: passwordInput },
      { responseType: 'text' } // Important if backend returns plain text
    );
    return response.data; // "User registered successfully!"
  };

  const logout = useCallback(() => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setAccessToken(null);
    setUser(null);
    // Optionally, redirect to login page via react-router's navigate
    // This is better handled in components or router logic
  }, []);

  // Effect to handle token expiry or invalidation from other tabs/windows
  useEffect(() => {
    const handleStorageChange = (event: StorageEvent) => {
      if (event.key === 'accessToken' && event.newValue === null) {
        logout();
      }
    };
    window.addEventListener('storage', handleStorageChange);
    return () => {
      window.removeEventListener('storage', handleStorageChange);
    };
  }, [logout]);


  return (
    <AuthContext.Provider value={{ isAuthenticated: !!accessToken && !!user, user, accessToken, login, register, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};