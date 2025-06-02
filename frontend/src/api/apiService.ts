// src/api/apiService.ts
import axios from 'axios';
import type { AxiosError, InternalAxiosRequestConfig } from 'axios';
import type { AuthResponse } from '../types';

const API_BASE_URL = 'http://localhost:49360';

const api = axios.create({
  baseURL: API_BASE_URL,
});

api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('accessToken');
    if (token && config.headers) { 
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true; // Помечаем, что это повторная попытка
      console.log("Attempting to refresh token...");

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const oldAccessToken = localStorage.getItem('accessToken'); // Истекший токен

        if (!refreshToken || !oldAccessToken) {
          console.error("No refresh token or old access token found. Logging out.");
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login'; // Принудительный редирект
          return Promise.reject(error);
        }
        
        console.log("Sending refresh token request with oldAccessToken:", oldAccessToken);
        console.log("And refreshToken:", refreshToken);

        
        const { data } = await axios.post<AuthResponse>( 
          `${API_BASE_URL}/refreshtoken/generateaccess`,
          {}, 
          {
            headers: {
              'X-Refresh-Token': refreshToken,
              'Authorization': `Bearer ${oldAccessToken}`, // Отправляем истекший JWT
            },
          }
        );
        
        console.log("New tokens received:", data);
        localStorage.setItem('accessToken', data.accessToken);
        if (data.refreshToken) { // Бэкенд может не всегда возвращать новый refreshToken
             localStorage.setItem('refreshToken', data.refreshToken);
        }

        // Обновляем заголовок Authorization в исходном запросе
        if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
        }
        
        // Повторяем исходный запрос с новым токеном
        console.log("Retrying original request with new token.");
        return api(originalRequest);

      } catch (refreshError: any) {
        console.error("Token refresh failed:", refreshError.response?.data || refreshError.message);
        // Если обновление токена не удалось, разлогиниваем пользователя
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        // TODO: Вызвать функцию logout из AuthContext или перенаправить на логин
        window.location.href = '/login'; // Принудительный редирект
        return Promise.reject(refreshError);
      }
    }

    // Для других ошибок просто возвращаем их
    return Promise.reject(error);
  }
);

export default api;