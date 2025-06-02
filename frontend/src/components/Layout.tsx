import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { LogOut, LayoutDashboard, ListCollapse, CandlestickChart, PieChart, Bitcoin, FolderOpenDot } from 'lucide-react'; // Icons

const Layout: React.FC = () => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <aside className="w-64 bg-gray-800 text-gray-100 flex flex-col">
        <div className="p-4 border-b border-gray-700">
          <h1 className="text-xl font-semibold">FinanceApp</h1>
          {user && <p className="text-sm text-gray-400">Welcome, {user.username}</p>}
        </div>
        <nav className="flex-1 p-4 space-y-2">
          <Link to="/dashboard" className="flex items-center px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700">
            <LayoutDashboard size={18} className="mr-3" /> Dashboard
          </Link>
          <Link to="/transactions" className="flex items-center px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700">
            <ListCollapse size={18} className="mr-3" /> Transactions
          </Link>
          <Link to="/monthly-stats" className="flex items-center px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700">
            <PieChart size={18} className="mr-3" /> Monthly Stats
          </Link>
          <Link to="/categories" className="flex items-center px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700">
            <FolderOpenDot size={18} className="mr-3" /> Categories
          </Link>
          <Link to="/portfolio" className="flex items-center px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700">
            <Bitcoin size={18} className="mr-3" /> Portfolio
          </Link>
          <Link to="/cryptocurrency/market" className="flex items-center px-3 py-2 rounded-md text-sm font-medium hover:bg-gray-700">
            <CandlestickChart size={18} className="mr-3" /> Crypto Market
          </Link>
        </nav>
        <div className="p-4 border-t border-gray-700">
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center px-3 py-2 rounded-md text-sm font-medium text-red-400 hover:bg-red-700 hover:text-white"
          >
            <LogOut size={18} className="mr-3" /> Logout
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 p-6 overflow-auto">
        <Outlet /> {/* This is where the routed page component will be rendered */}
      </main>
    </div>
  );
};

export default Layout;