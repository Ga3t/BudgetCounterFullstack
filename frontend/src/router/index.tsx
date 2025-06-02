// src/router/index.tsx
import React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import DashboardPage from '../pages/DashboardPage'; 
import TransactionsPage from '../pages/TransactionsPage'; 
import MonthlyStatsPage from '../pages/MonthlyStatsPage'; 
import CategoriesPage from '../pages/CategoriesPage'; 
import PortfolioPage from '../pages/PortfolioPage'; 
import CryptoMarketPage from '../pages/CryptoMarketPage'; 
import NotFoundPage from '../pages/NotFoundPage'; // Create this page
import ProtectedRoute from './ProtectedRoute';
import Layout from '../components/Layout'; // Create this component

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    element: <ProtectedRoute />, // Wraps routes that need authentication
    children: [
      {
        element: <Layout />, // Main app layout (with Navbar, etc.)
        children: [
          { path: '/', element: <DashboardPage /> }, // Or redirect to dashboard
          { path: '/dashboard', element: <DashboardPage /> },
          { path: '/transactions', element: <TransactionsPage /> },
          { path: '/monthly-stats/:year/:month', element: <MonthlyStatsPage /> },
          { path: '/monthly-stats', element: <MonthlyStatsPage /> }, // For form to select year/month
          { path: '/categories', element: <CategoriesPage /> },
          { path: '/portfolio', element: <PortfolioPage /> },
          { path: '/cryptocurrency/market', element: <CryptoMarketPage /> },
          // Add more protected routes here
        ]
      }
    ],
  },
  {
    path: '*', // Catch-all for 404
    element: <NotFoundPage />,
  },
]);

const AppRouter: React.FC = () => {
  return <RouterProvider router={router} />;
};

export default AppRouter;