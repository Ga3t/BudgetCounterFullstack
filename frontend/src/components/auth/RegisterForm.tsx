// src/components/auth/RegisterForm.tsx
import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Input from '../ui/Input';
import Button from '../ui/Button';
import { AxiosError } from 'axios';
import type { ApiErrorResponse } from '../../types';

const RegisterForm: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const { register } = useAuth(); // Assuming you added register to AuthContext
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    setError(null);
    setSuccessMessage(null);
    setIsLoading(true);

    try {
      const message = await register(username, password);
      setSuccessMessage(message + " You can now log in.");
      // Optionally redirect to login after a delay or let user click
      setTimeout(() => {
        navigate('/login');
      }, 3000);
    } catch (err) {
      const axiosError = err as AxiosError<ApiErrorResponse>;
      if (axiosError.response && axiosError.response.data && typeof axiosError.response.data === 'string') {
        setError(axiosError.response.data); // e.g. "Username already taken"
      } else if (axiosError.response && axiosError.response.data && axiosError.response.data.message) {
         setError(axiosError.response.data.message);
      } else {
        setError('Registration failed. Please try again.');
      }
      console.error("Registration error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form className="space-y-6" onSubmit={handleSubmit}>
      {error && <p className="text-center text-sm text-red-600 bg-red-100 p-3 rounded-md">{error}</p>}
      {successMessage && <p className="text-center text-sm text-green-600 bg-green-100 p-3 rounded-md">{successMessage}</p>}
      <Input
        label="Username"
        name="username"
        type="text"
        autoComplete="username"
        required
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <Input
        label="Password"
        name="password"
        type="password"
        autoComplete="new-password"
        required
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <Input
        label="Confirm Password"
        name="confirmPassword"
        type="password"
        autoComplete="new-password"
        required
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
      />
      <div>
        <Button type="submit" isLoading={isLoading} disabled={!!successMessage}>
          Register
        </Button>
      </div>
       <div className="text-sm text-center">
         <p className="text-gray-600">
            Already have an account?{' '}
            <Link to="/login" className="font-medium text-indigo-600 hover:text-indigo-500">
                Login here
            </Link>
        </p>
      </div>
    </form>
  );
};

export default RegisterForm;