
export const getMessages = async (token: string) => {
  console.log('token', token);

  const apiUrl: string = 'http://localhost:8080/api';

  const headers = new Headers();
  headers.append('Content-Type', 'application/json');
  headers.append('Authorization', `Bearer ${token}`);

  const response = await fetch(apiUrl, {
    method: 'GET',
    mode: 'cors',
    headers: headers
  });

  if (response.ok) {
    const json = await response.json();
    console.log('json', json);
  }
};
