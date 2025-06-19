const apiUrl: string = import.meta.env.VITE_BACKEND_API ?? '';

const getHeaders = (token: string | null): Headers => {
  const headers = new Headers();
  headers.append('Content-Type', 'application/json');
  headers.append('Authorization', `Bearer ${token}`);
  return headers;
}

export const signInOrSignUpUser = async (token: string | null) => {
  const response = await fetch(`${apiUrl}/user`, {
    method: 'POST',
    mode: 'cors',
    headers: getHeaders(token)
  });

  if (response.ok || response.status === 204) {
    return;
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const data = await response.json();
    throw new Error(data.message);
  }

  throw new Error('Something went wrong!');
};

export const getMessages = async (token: string | null) => {
  const response = await fetch(`${apiUrl}/messages`, {
    method: 'GET',
    mode: 'cors',
    headers: getHeaders(token)
  });

  if (response.ok) {
    const json = await response.json();
    console.log('json', json);
  }
};
