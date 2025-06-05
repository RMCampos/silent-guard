import { useState, useEffect } from 'react';
import LoginModal from './components/LoginModal';
import LandingPage from './pages/LandingPage';
import DashboardPage from './pages/DashboardPage';
import { useAuth0 } from '@auth0/auth0-react';

const App: React.FC = (): React.ReactNode => {
  const [currentPage, setCurrentPage] = useState<string>('landing');
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);
  const { isAuthenticated } = useAuth0();

  useEffect(() => {
    if (isAuthenticated) {
      setCurrentPage('dashboard');
    }
  }, [isAuthenticated]);

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
