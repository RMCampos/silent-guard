import React, { useState } from 'react';
import { Eye, EyeOff, X } from 'lucide-react';

type Props = {
  show: boolean;
  loginMode: string;
  onClose: () => void;
  onSubmitLogin: () => void;
}

type LoginForm = {
  email: string;
  password: string;
}

const emptyLoginForm: LoginForm = { email: '', password: '' };

const LoginModal: React.FC<Props> = (props) => {
  const [loginForm, setLoginForm] = useState<LoginForm>(emptyLoginForm);
  const [showPassword, setShowPassword] = useState<boolean>(false);

  return props.show
    ? (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl max-w-md w-full p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-900">
                {props.loginMode === 'login' ? 'Login' : 'Reset Password'}
              </h2>
              <button 
                onClick={() => props.onClose()}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            {props.loginMode === 'login' ? (
              <div className="space-y-4">
                <input
                  type="email"
                  placeholder="Email"
                  value={loginForm.email}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setLoginForm({...loginForm, email: e.target.value})}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Password"
                    value={loginForm.password}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setLoginForm({...loginForm, password: e.target.value})}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent pr-12"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-3 text-gray-400 hover:text-gray-600"
                  >
                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>
                <button
                  onClick={() => props.onSubmitLogin()}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition-colors"
                >
                  Login
                </button>
                <button
                  onClick={() => setLoginMode('reset')}
                  className="w-full text-blue-600 hover:text-blue-700 py-2 text-sm"
                >
                  Forgot your password?
                </button>
              </div>
            ) : (
              <div className="space-y-4">
                <input
                  type="email"
                  placeholder="Enter your email"
                  value={resetForm.email}
                  onChange={(e) => setResetForm({...resetForm, email: e.target.value})}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <button
                  onClick={handleResetPassword}
                  className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition-colors"
                >
                  Send Reset Link
                </button>
                <button
                  onClick={() => setLoginMode('login')}
                  className="w-full text-blue-600 hover:text-blue-700 py-2 text-sm"
                >
                  Back to login
                </button>
              </div>
            )}
          </div>
        </div>
    ) : null;
};

export default LoginModal;
