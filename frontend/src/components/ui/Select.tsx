import React from 'react'; 
import type { ReactNode } from 'react'; 

type SelectProps = React.SelectHTMLAttributes<HTMLSelectElement> & {
  label?: string;
  error?: string;
  children: ReactNode;
};

const Select: React.FC<SelectProps> = ({ label, name, error, children, ...props }) => {
  return (
    <div>
      {label && (
        <label htmlFor={name} className="block text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <select
        id={name}
        name={name}
        className={`mt-1 block w-full pl-3 pr-10 py-2 text-base border ${
          error ? 'border-red-500' : 'border-gray-300'
        } focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md`}
        {...props}
      >
        {children}
      </select>
      {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
    </div>
  );
};

export default Select;