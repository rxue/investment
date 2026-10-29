import MetricValuesTable from './MetricValuesTable.jsx'
import './App.css'

function App() {
  const metricData = {
    "AAPL": { "Stock price": "182.60", "P/E": "30.1" },
    "MSFT": { "Stock price": "415.20", "P/E": "35.4" }
};
  return <MetricValuesTable values = {metricData} />
}

export default App
