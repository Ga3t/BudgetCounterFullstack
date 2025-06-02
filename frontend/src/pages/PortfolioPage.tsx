// src/pages/PortfolioPage.tsx
import React, { useEffect, useState } from 'react';
import api from '../api/apiService';
import type { PortfolioItem, BuyCryptoPayload, SellCryptoPayload } from '../types'; // Ensure types are correct
import { format } from 'date-fns';
import { ArrowUpCircle, ArrowDownCircle, TrendingUp, Edit3, Bitcoin } from 'lucide-react'; 
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal'; 
import Input from '../components/ui/Input'; 

const mockCryptoOptions = [
    { id: "bitcoin", name: "Bitcoin" },
    { id: "ethereum", name: "Ethereum" },
    { id: "tether", name: "Tether" },
    // ... add more or fetch dynamically
];


const PortfolioPage: React.FC = () => {
  const [portfolio, setPortfolio] = useState<PortfolioItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isBuyModalOpen, setIsBuyModalOpen] = useState(false);
  const [isSellModalOpen, setIsSellModalOpen] = useState(false);
  const [transactionDetails, setTransactionDetails] = useState<Partial<BuyCryptoPayload & SellCryptoPayload>>({
    dateTime: format(new Date(), "yyyy-MM-dd'T'HH:mm:ss")
  });
  const [formError, setFormError] = useState<string | null>(null);


  const fetchPortfolio = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await api.get<PortfolioItem[]>('/portfolio/getportfolio');
      setPortfolio(response.data);
    } catch (err) {
      setError('Failed to load portfolio.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchPortfolio();
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setTransactionDetails(prev => ({
        ...prev,
        [name]: name === 'amount' ? parseFloat(value) : value
    }));
    setFormError(null);
  };

  const handleBuySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!transactionDetails.cryptoId || !transactionDetails.amount || transactionDetails.amount <=0 || !transactionDetails.dateTime) {
        setFormError("All fields are required and amount must be positive.");
        return;
    }
    setFormError(null);
    try {
        await api.post('/portfolio/buycrypto', transactionDetails);
        setIsBuyModalOpen(false);
        setTransactionDetails({ dateTime: format(new Date(), "yyyy-MM-dd'T'HH:mm:ss") }); // Reset
        fetchPortfolio(); // Refresh portfolio
        alert("Crypto bought successfully!");
    } catch (error) {
        console.error("Buy crypto error:", error);
        setFormError("Failed to buy crypto. " + ( (error as any).response?.data?.message || (error as Error).message));
    }
  };

  const handleSellSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!transactionDetails.cryptoId || !transactionDetails.amount || transactionDetails.amount <=0 || !transactionDetails.dateTime) {
        setFormError("All fields are required and amount must be positive.");
        return;
    }
     // Basic check: Do we have enough to sell?
    const assetToSell = portfolio.find(p => p.cryptoName === transactionDetails.cryptoId);
    if (!assetToSell || assetToSell.amount < (transactionDetails.amount || 0)) {
        setFormError("Not enough asset to sell or asset not in portfolio.");
        return;
    }

    setFormError(null);
    try {
        await api.post('/portfolio/sellcrypto', transactionDetails);
        setIsSellModalOpen(false);
        setTransactionDetails({ dateTime: format(new Date(), "yyyy-MM-dd'T'HH:mm:ss") }); // Reset
        fetchPortfolio(); // Refresh portfolio
        alert("Crypto sold successfully!");
    } catch (error) {
        console.error("Sell crypto error:", error);
        setFormError("Failed to sell crypto. " + ( (error as any).response?.data?.message || (error as Error).message));
    }
  };


  if (isLoading) return <div className="p-6">Loading portfolio...</div>;
  if (error) return <div className="p-6 text-red-500">{error}</div>;

  const totalPortfolioValue = portfolio.reduce((sum, item) => sum + (item.amount * item.currentPrice), 0);

  return (
    <div className="p-4 md:p-6">
      <div className="flex flex-col sm:flex-row justify-between items-center mb-6">
        <h1 className="text-2xl font-semibold text-gray-900">My Crypto Portfolio</h1>
        <div className="mt-2 sm:mt-0">
            <p className="text-sm text-gray-600">Total Value:</p>
            <p className="text-2xl font-bold text-indigo-700">
                {new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(totalPortfolioValue)}
            </p>
        </div>
      </div>

       <div className="mb-6 flex space-x-3">
          <Button onClick={() => { setTransactionDetails({ dateTime: format(new Date(), "yyyy-MM-dd'T'HH:mm:ss") }); setIsBuyModalOpen(true);}} variant="primary" className="w-auto">
            <ArrowUpCircle size={20} className="mr-2" /> Buy Crypto
          </Button>
          <Button onClick={() => { setTransactionDetails({ dateTime: format(new Date(), "yyyy-MM-dd'T'HH:mm:ss") }); setIsSellModalOpen(true);}} variant="secondary" className="w-auto">
            <ArrowDownCircle size={20} className="mr-2" /> Sell Crypto
          </Button>
        </div>

      {portfolio.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {portfolio.map((item) => (
            <div key={item.cryptoName} className="bg-white shadow-lg rounded-xl p-6 hover:shadow-xl transition-shadow duration-300">
              <div className="flex items-center justify-between mb-3">
                <h2 className="text-xl font-bold text-gray-800 capitalize">{item.cryptoName}</h2>
                <TrendingUp className="text-green-500" size={24} />
              </div>
              <div className="space-y-2 text-sm">
                <p><strong className="text-gray-600">Amount:</strong> {item.amount.toFixed(8)}</p>
                <p><strong className="text-gray-600">Current Price:</strong> {new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(item.currentPrice / item.amount)} (approx. per unit)</p>
                <p><strong className="text-gray-600">Total Value:</strong> {new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(item.currentPrice)}</p>
                <p><strong className="text-gray-600">Last Update:</strong> {format(new Date(item.last_update), 'Pp')}</p>
              </div>
              {/* <div className="mt-4 flex justify-end">
                <Button variant="secondary" size="xs" className="w-auto">
                  <Edit3 size={14} className="mr-1" /> Details / History
                </Button>
              </div> */}
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-10">
          <Bitcoin size={48} className="mx-auto text-gray-400 mb-4" />
          <p className="text-gray-500 text-lg">Your portfolio is empty.</p>
          <p className="text-gray-400">Start by buying some cryptocurrency!</p>
        </div>
      )}

      {/* Buy Crypto Modal */}
      <Modal isOpen={isBuyModalOpen} onClose={() => setIsBuyModalOpen(false)} title="Buy Cryptocurrency">
        <form onSubmit={handleBuySubmit} className="space-y-4">
          {formError && <p className="text-red-500 text-sm">{formError}</p>}
          <div>
            <label htmlFor="cryptoId-buy" className="block text-sm font-medium text-gray-700">Cryptocurrency</label>
            <select id="cryptoId-buy" name="cryptoId" value={transactionDetails.cryptoId || ""} onChange={handleInputChange} required
                    className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
                <option value="" disabled>Select Crypto</option>
                {mockCryptoOptions.map(crypto => <option key={crypto.id} value={crypto.id}>{crypto.name}</option>)}
            </select>
          </div>
          <Input label="Amount to Buy" name="amount" type="number" step="any" value={transactionDetails.amount || ""} onChange={handleInputChange} required />
          <Input label="Date & Time" name="dateTime" type="datetime-local" value={transactionDetails.dateTime || ""} onChange={handleInputChange} required 
            // Optional: Add max date constraint based on API (365 days before today)
          />
          <div className="flex justify-end space-x-2">
            <Button type="button" variant="secondary" onClick={() => setIsBuyModalOpen(false)}>Cancel</Button>
            <Button type="submit" variant="primary">Confirm Buy</Button>
          </div>
        </form>
      </Modal>

      {/* Sell Crypto Modal */}
      <Modal isOpen={isSellModalOpen} onClose={() => setIsSellModalOpen(false)} title="Sell Cryptocurrency">
        <form onSubmit={handleSellSubmit} className="space-y-4">
          {formError && <p className="text-red-500 text-sm">{formError}</p>}
           <div>
            <label htmlFor="cryptoId-sell" className="block text-sm font-medium text-gray-700">Cryptocurrency in Portfolio</label>
            <select id="cryptoId-sell" name="cryptoId" value={transactionDetails.cryptoId || ""} onChange={handleInputChange} required
                     className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md">
                <option value="" disabled>Select Crypto to Sell</option>
                {portfolio.map(item => <option key={item.cryptoName} value={item.cryptoName}>{item.cryptoName} (Available: {item.amount.toFixed(6)})</option>)}
            </select>
          </div>
          <Input label="Amount to Sell" name="amount" type="number" step="any" value={transactionDetails.amount || ""} onChange={handleInputChange} required />
          <Input label="Date & Time" name="dateTime" type="datetime-local" value={transactionDetails.dateTime || ""} onChange={handleInputChange} required 
            // Optional: Add min date constraint based on API (not before last_update for that crypto)
          />
           <div className="flex justify-end space-x-2">
            <Button type="button" variant="secondary" onClick={() => setIsSellModalOpen(false)}>Cancel</Button>
            <Button type="submit" variant="danger">Confirm Sell</Button>
          </div>
        </form>
      </Modal>

    </div>
  );
};

export default PortfolioPage;