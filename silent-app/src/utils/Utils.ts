import swal from 'sweetalert';

const showError = (message: string) => {
  swal('Oh no!', message, 'error');
}

export const handleAndDisplayError = (e: unknown) => {
  if (e instanceof Error) {
    showError(e.message);
  }
  else {
    showError(JSON.stringify(e));
  }
}
