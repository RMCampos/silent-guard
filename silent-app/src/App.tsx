import { useState, useEffect } from 'react';
import LoginModal from './components/LoginModal';
import AccountModal from './components/AccountModal';
import LandingPage from './pages/LandingPage';
import DashboardPage from './pages/DashboardPage';

const App = () => {
  const [currentPage, setCurrentPage] = useState<string>('landing');
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);
  const [showAccountModal, setShowAccountModal] = useState<boolean>(false);

  useEffect(() => {
    console.log('isAuthenticated', isAuthenticated);
    const auth = localStorage.getItem('silentGuardAuth');
    if (auth) {
      setIsAuthenticated(true);
      setCurrentPage('dashboard');
    }
  }, [isAuthenticated]);

  const handleLogin = () => {
    // e.preventDefault();

    // Simulate login
    setIsAuthenticated(true);
    setCurrentPage('dashboard');
    setShowLoginModal(false);
    // setLoginForm({ email: '', password: '' });
  };

  // const handleResetPassword = () => {
  //   alert('Password reset link sent to ' + resetForm.email);
  //   setResetForm({ email: '' });
  //   // setLoginMode('login');
  // };

  return (
    <div>
      {currentPage === 'landing'
        ?
          <LandingPage onClickLogin={() => setShowLoginModal(true)} />
          : <DashboardPage
              onClickAccount={() => setShowAccountModal(true)}
              setPageChanged={() => setCurrentPage('landing')}
            />
        }
      <LoginModal
        show={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        onSubmitLogin={handleLogin}
      />
      <AccountModal
        show={showAccountModal}
        onClose={() => setShowAccountModal(false)}
        onSubmitAccount={() => {}} // fix me
      />
    </div>
  );
  
};

export default App;
