// src/pages/MonthlyStatsPage.tsx
import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getYear, getMonth, format } from 'date-fns';

// TODO: Fetch and display monthly stats based on year/month params
// For now, a placeholder

const MonthlyStatsPage: React.FC = () => {
  const { year, month } = useParams<{ year: string; month: string }>();
  const navigate = useNavigate();

  // Simple form to select year and month if not provided in URL
  // Or, could have a default view for current month if no params
  const currentYear = getYear(new Date());
  const currentMonth = getMonth(new Date()) + 1;

  const handleFormSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const selectedYear = formData.get('year') as string;
    const selectedMonth = formData.get('month') as string;
    if (selectedYear && selectedMonth) {
      navigate(`/monthly-stats/${selectedYear}/${selectedMonth}`);
    }
  };


  return (
    <div className="p-4 md:p-6">
      <h1 className="text-2xl font-semibold text-gray-900 mb-4">
        Monthly Statistics
        {year && month && ` for ${format(new Date(parseInt(year), parseInt(month)-1), 'MMMM yyyy')}`}
      </h1>

      {!year || !month ? (
        <div className="bg-white shadow rounded-lg p-6">
            <h2 className="text-lg font-medium text-gray-700 mb-3">Select Month and Year</h2>
            <form onSubmit={handleFormSubmit} className="space-y-4 sm:flex sm:items-end sm:space-y-0 sm:space-x-3">
                <div>
                    <label htmlFor="year-select" className="block text-sm font-medium text-gray-700">Year</label>
                    <select 
                        id="year-select" 
                        name="year" 
                        defaultValue={currentYear}
                        className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
                    >
                        {[...Array(5)].map((_, i) => (
                            <option key={currentYear - i} value={currentYear - i}>{currentYear - i}</option>
                        ))}
                         {/* Add more years or make it dynamic */}
                    </select>
                </div>
                 <div>
                    <label htmlFor="month-select" className="block text-sm font-medium text-gray-700">Month</label>
                    <select 
                        id="month-select" 
                        name="month" 
                        defaultValue={currentMonth}
                        className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
                    >
                        {[...Array(12)].map((_, i) => (
                            <option key={i+1} value={i+1}>{format(new Date(2000, i), 'MMMM')}</option>
                        ))}
                    </select>
                </div>
                <button
                    type="submit"
                    className="w-full sm:w-auto inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                    Show Stats
                </button>
            </form>
        </div>
      ) : (
        <div className="mt-6 bg-white shadow rounded-lg p-6">
            <p className="text-center text-gray-500">
            Detailed statistics for {format(new Date(parseInt(year), parseInt(month)-1), 'MMMM yyyy')} will be displayed here.
            This will be similar to the stats section on the Dashboard but focused on the selected period.
            </p>
            {/* TODO: API call: /ledger/getmonthstat/{year}/{month} */}
        </div>
      )}
    </div>
  );
};

export default MonthlyStatsPage;