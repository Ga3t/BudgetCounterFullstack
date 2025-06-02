// src/pages/LoginPage.tsx
import React from 'react';
import LoginForm from '../components/auth/LoginForm'; 

const LoginPage: React.FC = () => {
  // ... остальной код без изменений
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          {/* <img className="mx-auto h-12 w-auto" src="https://tailwindui.com/img/logos/workflow-mark-indigo-600.svg" alt="Workflow" /> */}
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Sign in to your account
          </h2>
        </div>
        <LoginForm /> {/* <== Здесь */}
      </div>
    </div>
  );
};
export default LoginPage;