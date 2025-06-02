// src/pages/CryptoMarketPage.tsx
import React, { useEffect, useState, useCallback } from 'react';
import api from '../api/apiService';
import type { CryptoListItem, CryptoListResponse, CryptoDailyPrice } from '../types';
import { format } from 'date-fns';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { Eye } from 'lucide-react';
import Modal from '../components/ui/Modal';
import Button from '../components/ui/Button';

const CryptoMarketPage: React.FC = () => {
  const [cryptoList, setCryptoList] = useState<CryptoListItem[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [pagination, setPagination] = useState<{
    pageNo: number;
    pageSize: number;
    totalPages: number;
    totalElements: number;
  }>({
    pageNo: 0,
    pageSize: 10, // Соответствует вашему API ответу
    totalPages: 0,
    totalElements: 0,
  });

  const [selectedCrypto, setSelectedCrypto] = useState<CryptoListItem | null>(null);
  const [dailyPrices, setDailyPrices] = useState<CryptoDailyPrice[]>([]);
  const [isPriceModalOpen, setIsPriceModalOpen] = useState<boolean>(false); // Исправлено здесь
  const [isLoadingPrices, setIsLoadingPrices] = useState<boolean>(false);

  const fetchCryptoList = useCallback(async (page: number, size: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await api.get<CryptoListResponse>(`/cryptocurrency/getlist?page=${page}&size=${size}`);
      setCryptoList(response.data.content);
      setPagination({
        pageNo: response.data.number,
        pageSize: response.data.size,
        totalPages: response.data.totalPages,
        totalElements: response.data.totalElements,
      });
    } catch (err) {
      setError('Failed to load cryptocurrency list.');
      console.error("Fetch crypto list error:", err);
    } finally {
      setIsLoading(false);
    }
  }, []); // Зависимости useCallback остаются пустыми, если он не зависит от внешних пропсов/состояний (кроме тех, что внутри)

  useEffect(() => {
    fetchCryptoList(pagination.pageNo, pagination.pageSize);
  }, [fetchCryptoList, pagination.pageNo, pagination.pageSize]);


  const handleViewDailyPrice = async (crypto: CryptoListItem) => {
    setSelectedCrypto(crypto);
    setIsPriceModalOpen(true);
    setIsLoadingPrices(true);
    setDailyPrices([]); // Очищаем предыдущие данные
    try {
      const response = await api.get<CryptoDailyPrice[]>(`/cryptocurrency/dailyprice?cryptoId=${crypto.id}`);
      // Форматируем dateTime для отображения только времени и берем последние 20-50 точек для наглядности
      const formattedPrices = response.data
        .map(dp => ({ ...dp, dateTime: format(new Date(dp.dateTime), 'HH:mm') }))
        .slice(-30); // Показываем последние 30 точек
      setDailyPrices(formattedPrices);
    } catch (error) {
      console.error("Error fetching daily prices:", error);
      // Можно добавить состояние для отображения ошибки в модальном окне
    } finally {
        setIsLoadingPrices(false);
    }
  };

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < pagination.totalPages) {
      // setPagination обновит pageNo, а useEffect вызовет fetchCryptoList
      setPagination(prev => ({...prev, pageNo: newPage}));
    }
  }


  if (isLoading && cryptoList.length === 0) {
    return <div className="p-6 text-center">Loading cryptocurrency market data...</div>;
  }
  if (error) {
    return <div className="p-6 text-red-500 text-center">{error}</div>;
  }

  return (
    <div className="p-4 md:p-6">
      <h1 className="text-2xl font-semibold text-gray-900 mb-6">Cryptocurrency Market</h1>

      {cryptoList.length > 0 ? (
        <>
          <div className="bg-white shadow overflow-x-auto rounded-lg">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Icon</th>
                  <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                  <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Symbol</th>
                  <th scope="col" className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
                  <th scope="col" className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">24h %</th>
                  <th scope="col" className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Last Updated</th>
                  <th scope="col" className="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {cryptoList.map((crypto) => (
                  <tr key={crypto.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 whitespace-nowrap">
                      <img src={crypto.image} alt={crypto.name} className="h-8 w-8 rounded-full" />
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900">{crypto.name}</td>
                    <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-500">{crypto.symbol.toUpperCase()}</td>
                    <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700 text-right">
                      {new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', minimumFractionDigits: 2, maximumFractionDigits: crypto.current_price < 1 ? 6:2 }).format(crypto.current_price)}
                    </td>
                    <td className={`px-4 py-3 whitespace-nowrap text-sm text-right font-semibold ${crypto.price_change_percentage_24h >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                      {crypto.price_change_percentage_24h.toFixed(2)}%
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-500">{format(new Date(crypto.last_updated), 'Pp')}</td>
                    <td className="px-4 py-3 whitespace-nowrap text-sm font-medium text-center">
                      <Button size="xs" variant='secondary' onClick={() => handleViewDailyPrice(crypto)} className="w-auto inline-flex items-center">
                        <Eye size={16} className="mr-1"/> Chart
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination Controls */}
          {pagination.totalPages > 1 && (
              <div className="mt-6 flex justify-center items-center space-x-2">
                  <Button
                      onClick={() => handlePageChange(pagination.pageNo - 1)}
                      disabled={pagination.pageNo === 0 || isLoading}
                      variant="secondary"
                      className="w-auto px-4 py-2 text-sm"
                  >
                      Previous
                  </Button>
                  <span className="text-sm text-gray-700">
                      Page {pagination.pageNo + 1} of {pagination.totalPages}
                  </span>
                  <Button
                      onClick={() => handlePageChange(pagination.pageNo + 1)}
                      disabled={pagination.pageNo + 1 >= pagination.totalPages || isLoading}
                      variant="secondary"
                      className="w-auto px-4 py-2 text-sm"
                  >
                      Next
                  </Button>
              </div>
          )}
        </>
      ) : (
        !isLoading && <p className="text-gray-500 text-center py-10">No cryptocurrencies found.</p>
      )}


      {/* Daily Price Modal */}
      <Modal 
        isOpen={isPriceModalOpen} 
        onClose={() => {
          setIsPriceModalOpen(false);
          setSelectedCrypto(null); // Сбрасываем выбранную крипту при закрытии
        }} 
        title={`Price Chart: ${selectedCrypto?.name || ''} (${selectedCrypto?.symbol.toUpperCase() || ''})`} 
        size="xl"
      >
        {isLoadingPrices && <p className="text-center py-10">Loading price data...</p>}
        {!isLoadingPrices && dailyPrices.length > 0 && (
          <ResponsiveContainer width="100%" height={400}>
            <LineChart data={dailyPrices} margin={{ top: 5, right: 30, left: 50, bottom: 5 }}> {/* Увеличен левый отступ для YAxis */}
              <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
              <XAxis dataKey="dateTime" tick={{ fontSize: 12 }} />
              <YAxis 
                domain={['auto', 'auto']} 
                tickFormatter={(value) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value)}
                tick={{ fontSize: 12 }}
                width={80} // Даем больше места для тиков
              />
              <Tooltip 
                formatter={(value: number) => [
                    new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', minimumFractionDigits: 2, maximumFractionDigits: value < 0.01 ? 8 : 2 }).format(value),
                    "Price"
                ]} 
                labelFormatter={(label: string) => `Time: ${label}`}
              />
              <Legend />
              <Line type="monotone" dataKey="price" stroke="#4f46e5" strokeWidth={2} activeDot={{ r: 6 }} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        )}
         {!isLoadingPrices && dailyPrices.length === 0 && selectedCrypto && (
            <p className="text-center py-10 text-gray-500">No recent price data available for {selectedCrypto.name}.</p>
         )}
      </Modal>
    </div>
  );
};

export default CryptoMarketPage;