import { Routes, Route } from 'react-router'
import Home from './Home.jsx'
import Quotes from './Quotes.jsx'
import Benchmark from './Benchmark.jsx'

function App() {
  return (
    <Routes>
      <Route index element={<Home />} />
      <Route path="/quotes" element={<Quotes />} />
      <Route path="/benchmark" element={<Benchmark />} />
    </Routes>
  )
}

export default App
