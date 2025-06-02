// src/pages/DashboardPage.tsx
import React, { useEffect, useState } from 'react';
import api from '../api/apiService';
import type { MonthStatsResponse, CategoryCircleItem, Transaction, TransactionsResponse, Category, CreateTransactionPayload } from '../types';
import { format, getYear, getMonth } from 'date-fns';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { PlusCircle, Filter, Edit3, Trash2, ChevronDown, ChevronUp } from 'lucide-react';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import Textarea from '../components/ui/Textarea';
import Select from '../components/ui/Select';

const formatCurrency = (value: number | undefined | null) => { // Добавил null в тип
  if (value === undefined || value === null) return '$0.00'; // Или 'N/A', или то, что вы хотите видеть для пустых значений
  // Пример для USD. Если нужно RUB, измените 'en-US' на 'ru-RU' и 'USD' на 'RUB'
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
};

const formatPercentage = (value: number | undefined | null) => { // Добавил null в тип
  if (value === undefined || value === null) return '0.00%'; // Или 'N/A'
  return `${value.toFixed(2)}%`;
};

const COLORS_INCOME = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#AF19FF', '#FF4F81'];
const COLORS_EXPENSES = ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40', '#C9CBCF'];


const DashboardPage: React.FC = () => {
  const [stats, setStats] = useState<MonthStatsResponse | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoadingStats, setIsLoadingStats] = useState(true);
  const [isLoadingTransactions, setIsLoadingTransactions] = useState(true);
  const [errorStats, setErrorStats] = useState<string | null>(null);
  const [errorTransactions, setErrorTransactions] = useState<string | null>(null);

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [newTransaction, setNewTransaction] = useState<Partial<CreateTransactionPayload>>({
    time: format(new Date(), "yyyy-MM-dd'T'HH:mm:ss"),
  });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [expandedTransactionId, setExpandedTransactionId] = useState<number | null>(null);

  const currentYear = getYear(new Date());
  const currentMonth = getMonth(new Date()) + 1;

  // Объявим fetchStats и fetchTransactions здесь, чтобы использовать их в handleSubmit
  const fetchStats = async () => {
    setIsLoadingStats(true);
    setErrorStats(null);
    try {
      console.log(`Fetching stats for: /ledger/getmonthstat/${currentYear}/${currentMonth}`);
      const response = await api.get<MonthStatsResponse>(`/ledger/getmonthstat/${currentYear}/${currentMonth}`);
      console.log("<<< Raw stats response data from API >>>:", response.data);
      setStats(response.data);
    } catch (error) {
      console.error("Error fetching monthly stats:", error);
      setErrorStats('Failed to load monthly statistics.');
      setStats(null); // Убедимся, что stats сбрасывается при ошибке
    } finally {
      setIsLoadingStats(false);
    }
  };

  const fetchTransactions = async () => {
    setIsLoadingTransactions(true);
    setErrorTransactions(null);
    try {
      console.log("Fetching transactions from: /ledger/transactions");
      const response = await api.get<TransactionsResponse>('/ledger/transactions');
      console.log("<<< Raw transactions response data from API >>>:", response.data);
      setTransactions(response.data.infoLedgerDTO);
    } catch (error) {
      console.error("Error fetching transactions:", error);
      setErrorTransactions('Failed to load transactions.');
    } finally {
      setIsLoadingTransactions(false);
    }
  };


  useEffect(() => {
    const fetchCategories = async () => {
        try {
            console.log("Fetching categories from: /category/getallcategory");
            const response = await api.get<Category[]>('/category/getallcategory');
            console.log("<<< Raw categories response data from API >>>:", response.data);
            setCategories(response.data);
        } catch (error) {
            console.error("Error fetching categories:", error);
        }
    };

    fetchStats();
    fetchTransactions();
    fetchCategories();
  }, [currentYear, currentMonth]); // fetchStats и fetchTransactions будут пересоздаваться, если их не вынести или не использовать useCallback

  // --- Логирование состояния и данных для диаграмм ---
  console.log("Current stats state in component body:", stats);
  const incomeData = stats?.categoryCircle?.filter(c => c.categoryType === 'INCOME').map(c => ({ name: c.categoryName, value: c.sum })) || [];
  const expenseData = stats?.categoryCircle?.filter(c => c.categoryType === 'EXPENSES').map(c => ({ name: c.categoryName, value: c.sum })) || [];
  console.log("Income data for chart:", incomeData);
  console.log("Expense data for chart:", expenseData);
  // -------------------------------------------------


  const handleCreateTransactionChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setNewTransaction(prev => ({ ...prev, [name]: name === 'price' ? parseFloat(value) : value }));
    if (formErrors[name]) {
        setFormErrors(prev => ({...prev, [name]: ''}));
    }
  };

  const validateForm = (): boolean => {
    const errors: Record<string, string> = {};
    if (!newTransaction.name?.trim()) errors.name = "Name is required.";
    if (newTransaction.price === undefined || newTransaction.price <= 0) errors.price = "Price must be a positive number.";
    if (!newTransaction.time) errors.time = "Time is required.";
    if (!newTransaction.categoryName) errors.categoryName = "Category is required.";
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  }

  const handleCreateTransactionSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
        await api.post('/ledger/createtran', newTransaction);
        setIsCreateModalOpen(false);
        setNewTransaction({ time: format(new Date(), "yyyy-MM-dd'T'HH:mm:ss") }); // Reset form
        // Обновляем данные после успешного создания
        await fetchTransactions();
        await fetchStats();
        alert("Transaction successfully saved!");
    } catch (error) {
        console.error("Error creating transaction:", error);
        const errorMessage = (error as any).response?.data?.message || (error as any).message || "Failed to save transaction.";
        setFormErrors(prev => ({...prev, general: errorMessage}));
    }
  };

  const toggleTransactionDetails = (id: number) => {
    setExpandedTransactionId(expandedTransactionId === id ? null : id);
  };

  // Отображение загрузки, если isLoadingStats еще true или stats еще не установлен
  if (isLoadingStats || !stats) {
    // Добавил !stats, так как isLoadingStats может стать false до того, как setStats завершится
    // или если произошла ошибка и stats остался null
    return (
        <div className="p-6 text-center">
            {errorStats ? (
                <p className="text-red-500">{errorStats}</p>
            ) : (
                <p>Loading dashboard data...</p>
            )}
        </div>
    );
  }


  return (
    <div className="p-4 md:p-6 space-y-6">
      <section className="bg-white shadow rounded-lg p-6">
        <h2 className="text-xl font-semibold text-gray-700 mb-4">Monthly Overview ({format(new Date(currentYear, currentMonth - 1), 'MMMM yyyy')})</h2>
        {/* errorStats уже обрабатывается в условии загрузки выше, но можно оставить для специфических ошибок, если stats не null */}
        {stats && ( // Проверка на stats здесь для безопасности, хотя выше уже есть
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 text-center">
            <div className="p-4 bg-green-50 rounded-lg">
              <p className="text-sm text-green-600">Total Income</p>
              <p className="text-2xl font-bold text-green-800">{formatCurrency(stats.totalIncomeForMonth)}</p>
            </div>
            <div className="p-4 bg-red-50 rounded-lg">
              <p className="text-sm text-red-600">Total Expenses</p>
              <p className="text-2xl font-bold text-red-800">{formatCurrency(stats.totalExpensesForMonth)}</p>
            </div>
            <div className="p-4 bg-blue-50 rounded-lg">
              <p className="text-sm text-blue-600">Difference</p>
              <p className={`text-2xl font-bold ${stats.difference >= 0 ? 'text-blue-800' : 'text-orange-600'}`}>{formatCurrency(stats.difference)}</p>
            </div>
             <div className="p-4 bg-indigo-50 rounded-lg">
              <p className="text-sm text-indigo-600">Income Change</p>
              <p className={`text-xl font-bold ${stats.incomeChange >= 0 ? 'text-indigo-800' : 'text-orange-600'}`}>{formatPercentage(stats.incomeChange)}</p>
            </div>
            <div className="p-4 bg-purple-50 rounded-lg">
              <p className="text-sm text-purple-600">Expenses Change</p>
              <p className={`text-xl font-bold ${stats.expensesChange >= 0 ? 'text-purple-800' : 'text-orange-600'}`}>{formatPercentage(stats.expensesChange)}</p>
            </div>
            <div className="p-4 bg-gray-50 rounded-lg">
              <p className="text-sm text-gray-600">Biggest Income</p>
              <p className="text-xl font-bold text-gray-800">{formatCurrency(stats.biggestIncome)}</p>
            </div>
            <div className="p-4 bg-gray-50 rounded-lg">
              <p className="text-sm text-gray-600">Biggest Expense</p>
              <p className="text-xl font-bold text-gray-800">{formatCurrency(stats.biggestExpenses)}</p>
            </div>
          </div>
        )}
      </section>

      {/* Секция круговых диаграмм: убедимся, что stats и categoryCircle существуют */}
      {stats && stats.categoryCircle && (incomeData.length > 0 || expenseData.length > 0) && (
        <section className="grid md:grid-cols-2 gap-6">
          {incomeData.length > 0 && (
            <div className="bg-white shadow rounded-lg p-6">
              <h3 className="text-lg font-semibold text-gray-700 mb-4">Income by Category</h3>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie data={incomeData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={100} fill="#8884d8" labelLine={false}
                       label={({ cx, cy, midAngle, innerRadius, outerRadius, percent, index }) => {
                            const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
                            const x = cx + radius * Math.cos(-midAngle * Math.PI / 180);
                            const y = cy + radius * Math.sin(-midAngle * Math.PI / 180);
                            return (
                              <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central" fontSize="12px">
                                {`${(percent * 100).toFixed(0)}%`}
                              </text>
                            );
                       }}>
                    {incomeData.map((entry, index) => (
                      <Cell key={`cell-income-${index}`} fill={COLORS_INCOME[index % COLORS_INCOME.length]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value: number, name: string) => [formatCurrency(value), name]} />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          )}
          {expenseData.length > 0 && (
             <div className="bg-white shadow rounded-lg p-6">
              <h3 className="text-lg font-semibold text-gray-700 mb-4">Expenses by Category</h3>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie data={expenseData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={100} fill="#82ca9d" labelLine={false}
                       label={({ cx, cy, midAngle, innerRadius, outerRadius, percent, index }) => {
                            const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
                            const x = cx + radius * Math.cos(-midAngle * Math.PI / 180);
                            const y = cy + radius * Math.sin(-midAngle * Math.PI / 180);
                            return (
                              <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central" fontSize="12px">
                                {`${(percent * 100).toFixed(0)}%`}
                              </text>
                            );
                       }}>
                    {expenseData.map((entry, index) => (
                      <Cell key={`cell-expense-${index}`} fill={COLORS_EXPENSES[index % COLORS_EXPENSES.length]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value: number, name: string) => [formatCurrency(value), name]} />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          )}
        </section>
      )}
      {stats && stats.categoryCircle && incomeData.length === 0 && expenseData.length === 0 && (
        <section className="bg-white shadow rounded-lg p-6 text-center">
            <p className="text-gray-500">No category data available to display charts for this month.</p>
        </section>
      )}


      <section className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-4">
        <Button onClick={() => { setFormErrors({}); setNewTransaction({ time: format(new Date(), "yyyy-MM-dd'T'HH:mm:ss") }); setIsCreateModalOpen(true);}} variant="primary" className="w-full sm:w-auto">
            <PlusCircle size={20} className="mr-2" /> Create Transaction
        </Button>
        <Button variant="secondary" className="w-full sm:w-auto" onClick={() => { alert("Filter functionality (transactions page) to be implemented")}}>
            <Filter size={20} className="mr-2" /> Filter Transactions
        </Button>
      </section>

       <Modal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} title="Create New Transaction">
            <form onSubmit={handleCreateTransactionSubmit} className="space-y-4">
                <Input
                    label="Name (Short Title)"
                    name="name"
                    value={newTransaction.name || ''}
                    onChange={handleCreateTransactionChange}
                    error={formErrors.name}
                    required
                    placeholder="e.g., Groceries, Salary"
                />
                <Input
                    label="Price"
                    name="price"
                    type="number"
                    step="0.01"
                    value={newTransaction.price || ''}
                    onChange={handleCreateTransactionChange}
                    error={formErrors.price}
                    required
                    placeholder="e.g., 50.75"
                />
                <Textarea
                    label="Description (Optional)"
                    name="description"
                    value={newTransaction.description || ''}
                    onChange={handleCreateTransactionChange}
                    error={formErrors.description}
                    placeholder="e.g., Weekly shopping at Walmart"
                />
                <Input
                    label="Time"
                    name="time"
                    type="datetime-local"
                    value={newTransaction.time || ''}
                    onChange={handleCreateTransactionChange}
                    error={formErrors.time}
                    required
                />
                <Select
                    label="Category"
                    name="categoryName"
                    value={newTransaction.categoryName || ''}
                    onChange={handleCreateTransactionChange}
                    error={formErrors.categoryName}
                    required
                >
                    <option value="" disabled>Select a category</option>
                    {categories.length > 0 ? categories.map(cat => (
                        <option key={cat.id} value={cat.name}>{cat.name} ({cat.categoryType})</option>
                    )) : <option disabled>Loading categories...</option>}
                </Select>
                {formErrors.general && <p className="text-sm text-red-500 p-2 bg-red-50 rounded">{formErrors.general}</p>}
                <div className="flex justify-end space-x-3 pt-2">
                    <Button type="button" variant="secondary" onClick={() => setIsCreateModalOpen(false)}>Cancel</Button>
                    <Button type="submit" variant="primary">Save Transaction</Button>
                </div>
            </form>
        </Modal>

      <section className="bg-white shadow rounded-lg p-6">
        <h2 className="text-xl font-semibold text-gray-700 mb-4">Recent Transactions</h2>
        {isLoadingTransactions && <p className="text-center">Loading transactions...</p>}
        {errorTransactions && <p className="text-red-500 text-center">{errorTransactions}</p>}
        {!isLoadingTransactions && !errorTransactions && transactions.length === 0 && (
          <p className="text-gray-500 text-center py-5">No transactions found for your account yet.</p>
        )}
        {!isLoadingTransactions && transactions.length > 0 && (
          <div className="space-y-3">
            {transactions.map((t) => (
              <div key={t.id} className="border border-gray-200 rounded-md hover:shadow-sm transition-shadow">
                <div
                  className="flex justify-between items-center p-3 cursor-pointer hover:bg-gray-50"
                  onClick={() => toggleTransactionDetails(t.id)}
                >
                  <div>
                    <span className="font-medium text-gray-800">{t.shortName}</span>
                    <span className={`ml-3 text-sm font-semibold ${t.categoryType === 'INCOME' ? 'text-green-600' : 'text-red-600'}`}>
                      {formatCurrency(t.price)}
                    </span>
                  </div>
                  {expandedTransactionId === t.id ? <ChevronUp size={20} className="text-gray-500"/> : <ChevronDown size={20} className="text-gray-500"/>}
                </div>
                {expandedTransactionId === t.id && (
                  <div className="p-4 border-t border-gray-200 bg-gray-50 text-sm text-gray-700 space-y-1">
                    <p><strong>Date:</strong> {format(new Date(t.transactionDate), 'MMMM dd, yyyy HH:mm')}</p>
                    <p><strong>Category:</strong> {t.categoryName} <span className={`text-xs px-1.5 py-0.5 rounded-full ${t.categoryType === 'INCOME' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>{t.categoryType}</span></p>
                    {t.description && <p><strong>Description:</strong> {t.description}</p>}
                    <div className="pt-2 flex space-x-2">
                      <Button variant='secondary' size="xs" className="w-auto" onClick={(e) => {e.stopPropagation(); alert(`Edit TX ID: ${t.id} - Functionality TBD`);}}>
                          <Edit3 size={14} className="mr-1"/> Edit
                      </Button>
                        <Button variant='danger' size="xs" className="w-auto" onClick={(e) => {e.stopPropagation(); alert(`Delete TX ID: ${t.id} - Functionality TBD`);}}>
                          <Trash2 size={14} className="mr-1"/> Delete
                          </Button>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default DashboardPage;