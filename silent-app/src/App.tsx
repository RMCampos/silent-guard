import React, { useState, useEffect } from 'react';
import LoginModal from './components/LoginModal';
import LandingPage from './pages/LandingPage';
import DashboardPage from './pages/DashboardPage';
import { useAuth0 } from '@auth0/auth0-react';

/**
 * The main App part that manages the application state and renders the appropriate page.
 * It uses Auth0 for authentication and conditionally renders the landing or dashboard page based on
 * the authentication status.
 *
 * @returns {React.ReactNode} The rendered App component.
 */
const App: React.FC = (): React.ReactNode => {
  const [currentPage, setCurrentPage] = useState<string>('landing');
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);
  const { isAuthenticated, isLoading } = useAuth0();

  useEffect(() => {
    if (isLoading) {
      return;
    }

    if (isAuthenticated) {
      setCurrentPage('dashboard');
    }
  }, [isAuthenticated, isLoading]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-slate-900">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-400 mx-auto"></div>
          <p className="mt-2 text-slate-300">Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div>
      {currentPage === 'landing'
        ?
          <LandingPage />
          : <DashboardPage setPageChanged={() => setCurrentPage('landing')} />
        }
      <LoginModal
        show={showLoginModal}
        onClose={() => setShowLoginModal(false)}
      />
    </div>
  );
};

export default App;
