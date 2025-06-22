import type { Message } from "../types/Message";

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
    // TODO: create list of recipients manually
    return json;
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const data = await response.json();
    throw new Error(data.message);
  }

  throw new Error('Something went wrong!');
};

export const createMessage = async (token: string | null, payload: Message) => {
  const response = await fetch(`${apiUrl}/messages`, {
    method: 'PUT',
    mode: 'cors',
    headers: getHeaders(token),
    body: JSON.stringify(payload)
  });

  if (response.ok) {
    const json = await response.json();
    return json;
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const data = await response.json();
    throw new Error(data.message);
  }

  throw new Error('Something went wrong!');
}

export const updateMessage = async (token: string | null, payload: Message) => {
  const response = await fetch(`${apiUrl}/messages/${payload.id}`, {
    method: 'POST',
    mode: 'cors',
    headers: getHeaders(token),
    body: JSON.stringify(payload)
  });

  if (response.ok && response.status === 200) {
    const json = await response.json();
    return json;
  }

  if (response.status === 204) {
    return;
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const data = await response.json();
    throw new Error(data.message);
  }

  throw new Error('Something went wrong!');
};

export const deleteMessage = async (token: string | null, id: number) => {
  const response = await fetch(`${apiUrl}/messages/${id}`, {
    method: 'DELETE',
    mode: 'cors',
    headers: getHeaders(token)
  });

  if (response.status === 204) {
    return;
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const data = await response.json();
    throw new Error(data.message);
  }

  throw new Error('Something went wrong!');
}
