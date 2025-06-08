import React, { createContext, useContext, useEffect, useState } from "react";
import { useAuth0 } from "@auth0/auth0-react";

interface TokenContextData {
  accessToken: string | null;
}

const TokenContext = createContext<TokenContextData | undefined>(undefined);

interface TokenProviderProps {
  children: React.ReactNode;
}

const TokenProvider = ({ children }: TokenProviderProps) => {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const { getAccessTokenSilently, isAuthenticated, isLoading } = useAuth0();

  useEffect(() => {
    const getToken = async () => {
      if (isAuthenticated && !isLoading) {
        try {
          const token: string = await getAccessTokenSilently({
            authorizationParams: {
              scope: 'profile email',
              audience: 'http://localhost:8080',
            }
          });
          setAccessToken(token);
        } catch (error) {
          console.error(error);
        }
      }
    };

    getToken();
  }, [isAuthenticated, isLoading, getAccessTokenSilently]);

  return (
    <TokenContext.Provider value={{ accessToken }}>
      {children}
    </TokenContext.Provider>
  );
};

const useToken = (): TokenContextData => {
  const context = useContext(TokenContext);
  if (!context) {
    throw new Error('useToken must be used within TokenProvider');
  }
  return context;
};

// eslint-disable-next-line react-refresh/only-export-components
export { TokenProvider, useToken };
