import Swal from 'sweetalert2'
import withReactContent from 'sweetalert2-react-content'

export const MySwal = withReactContent(Swal)

/**
 * This function is used to display a popup with a title, icon, text, and a button with a specific
 * color.
 * @param message - The message to display in the alert.
 * @param fn - The function to call when an alert is closed.
 */
export function FireError(message) {
  const swal = MySwal.fire({
    title: '¡Error!',
    icon: 'error',
    text: message,
    confirmButtonColor: '#AB3428'
  })

  return swal
}
