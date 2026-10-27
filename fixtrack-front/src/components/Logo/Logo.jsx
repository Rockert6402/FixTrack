import { MdBuild } from 'react-icons/md'
import './Logo.css'

export default function Logo({ variant = 'light' }) {
  return (
    <span className={`logo logo--${variant}`}>
      <span className="logo__mark" aria-hidden="true">
        <MdBuild />
      </span>
      <span className="logo__text">FixTrack</span>
    </span>
  )
}
