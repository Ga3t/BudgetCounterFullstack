// src/pages/CategoriesPage.tsx
import React, { useEffect, useState } from 'react';
import api from '../api/apiService';
import type { Category } from '../types'; // Make sure Category type is defined in src/types/index.ts

const CategoriesPage: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCategories = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await api.get<Category[]>('/category/getallcategory');
        setCategories(response.data);
      } catch (err) {
        setError('Failed to load categories.');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };
    fetchCategories();
  }, []);

  if (isLoading) return <div className="p-6">Loading categories...</div>;
  if (error) return <div className="p-6 text-red-500">{error}</div>;

  const incomeCategories = categories.filter(cat => cat.categoryType === 'INCOME');
  const expenseCategories = categories.filter(cat => cat.categoryType === 'EXPENSES');

  return (
    <div className="p-4 md:p-6">
      <h1 className="text-2xl font-semibold text-gray-900 mb-6">Manage Categories</h1>
      
      {/* TODO: Add functionality to create/edit/delete categories if API supports it */}
      {/* <div className="mb-6">
        <Button variant="primary">Add New Category</Button>
      </div> */}

      <div className="grid md:grid-cols-2 gap-6">
        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-xl font-semibold text-green-700 mb-4">Income Categories</h2>
          {incomeCategories.length > 0 ? (
            <ul className="space-y-2">
              {incomeCategories.map((category) => (
                <li key={category.id} className="p-3 bg-green-50 rounded-md flex justify-between items-center">
                  <span className="text-green-800">{category.name}</span>
                  {/* Add edit/delete icons here if needed */}
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500">No income categories found.</p>
          )}
        </div>

        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-xl font-semibold text-red-700 mb-4">Expense Categories</h2>
          {expenseCategories.length > 0 ? (
            <ul className="space-y-2">
              {expenseCategories.map((category) => (
                <li key={category.id} className="p-3 bg-red-50 rounded-md flex justify-between items-center">
                  <span className="text-red-800">{category.name}</span>
                   {/* Add edit/delete icons here if needed */}
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500">No expense categories found.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default CategoriesPage;