import React from 'react';

const TransactionsPage: React.FC = () => {
  return (
    <div className="p-4 md:p-6">
      <h1 className="text-2xl font-semibold text-gray-900 mb-4">Transactions</h1>
      <p className="text-gray-600">
        Here you will see a list of all your transactions. You will be able to filter, sort, and manage them.
      </p>
      {/* TODO: Implement transaction list, filters, pagination, create/edit/delete functionality */}
      <div className="mt-6 bg-white shadow rounded-lg p-6">
        <p className="text-center text-gray-500">Transaction management interface will be here.</p>
      </div>
    </div>
  );
};

export default TransactionsPage;