import { useEffect } from 'react'

interface ToastProps {
  message: string
  onClose: () => void
  durationMs?: number
}

export default function Toast({ message, onClose, durationMs = 3000 }: ToastProps) {
  useEffect(() => {
    const timer = setTimeout(onClose, durationMs)
    return () => clearTimeout(timer)
  }, [message, onClose, durationMs])

  return (
    <div className="toast toast-warning" role="alert">
      <span>{message}</span>
      <button className="toast-close" onClick={onClose} aria-label="Dismiss">✕</button>
    </div>
  )
}
