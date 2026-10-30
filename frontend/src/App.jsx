import { useEffect, useState } from 'react'
import MetricValuesTable from './MetricValuesTable.jsx'
import './App.css'

class QuotePrice {
  constructor({ value, currency }) {
    this.value = value;
    this.currency = currency;
  }

  format() {
    return `${this.value} ${this.currency}`;
  }
}

class NumberWithFormat {
  constructor({ value, formatted }) {
    this.value = value;
    this.formatted = formatted;
  }

  format() {
    return this.formatted;
  }
}

function formatMetric(metric) {
  if (typeof metric !== "object" || metric === null) return metric;
  if (metric.formatted) return new NumberWithFormat(metric).format();
  return new QuotePrice(metric).format();
}

function App() {
  const [quotes, setQuotes] = useState(null);
  useEffect(() => {
    fetch("http://localhost:8080/marketquotes?securityIds=KCR.HE,ELISA.HE,NOVO-B.CO,MANTA.HE,FIA1S.HE&metrics=COMPANY_NAME,LATEST_MARKET_PRICE,REGULAR_MARKET_CHANGE_PERCENT")
      .then(response => response.json())
      .then(data => setQuotes(data))
      .catch(error => console.error(error));
  }, []);
  if (!quotes) return <p>Loading...</p>;
  console.log("entries:::" + Array.isArray(quotes));
  quotes.forEach(quote => console.log(quote));
  const metricData = Object.fromEntries(
    quotes.map(({ securityId, valueByMetric }) => [
      securityId,
      Object.fromEntries(
        Object.entries(valueByMetric).map(([name, metric]) => [name, formatMetric(metric)])
      )
    ])
  );
  return <MetricValuesTable metricData = {metricData} />
}

export default App
